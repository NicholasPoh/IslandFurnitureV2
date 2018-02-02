package B_servlets;
import HelperClasses.Furniture;
import HelperClasses.ShoppingCartLineItem;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Author: Koh Ding Yuan
//Editor: Nicholas Poh
@WebServlet(name = "ECommerce_AddFurnitureToListServlet", urlPatterns = {"/ECommerce_AddFurnitureToListServlet"})
public class ECommerce_AddFurnitureToListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String sku = request.getParameter("SKU");
        int[] storeIds = {59, 60, 61}; //Hard coded
        String category = "";
        HttpSession session = request.getSession();
        Client client = ClientBuilder.newClient();
        try {
            int availableQuantity = 0;
            for(int storeId: storeIds){
                WebTarget target = client
                    .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.storeentity")
                    .path("getQuantity")
                    .queryParam("storeID", storeId)
                    .queryParam("SKU", sku);
                Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
                Response returned = invocationBuilder.get();
                System.out.println("status: " + returned.getStatus());
                if (returned.getStatus() != 200) {
                }
                String result = (String) returned.readEntity(String.class);
                availableQuantity += Integer.parseInt(result);
            }
            if(availableQuantity == 0){
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart."
                + "jsp?&errMsg=Item not added to cart, not enough quantity available. :( " );
            }
            else {
                try{
                    String id = request.getParameter("id");
                    int quantity = 0;
                    sku = request.getParameter("SKU");
                    double price = Double.parseDouble(request.getParameter("price"));
                    String name = request.getParameter("name");
                    String imageUrl = request.getParameter("imageURL");
                    ArrayList<ShoppingCartLineItem> shoppingCart ;
                    if(session.getAttribute("shoppingCart") == null){
                        shoppingCart = new ArrayList<>();
                        quantity = 1;
                        
                        ShoppingCartLineItem item = new ShoppingCartLineItem();
                        item.setId(id);
                        item.setSKU(sku);
                        item.setPrice(price);
                        item.setName(name);
                        item.setImageURL(imageUrl);
                        item.setQuantity(quantity);
                        shoppingCart.add(item);
                        session.setAttribute("shoppingCart", shoppingCart);
                        session.setAttribute("maxQuantity", quantity);
                        category = (String) request.getParameter("category");
                        response.sendRedirect("/IS3102_Project-war/B/SG/furnitureCategory."
                            + "jsp?cat=" + category
                            + "&goodMsg=Item added to cart. :)"); 
                    }
                    else{
                        shoppingCart = (ArrayList<ShoppingCartLineItem>)
                            session.getAttribute("shoppingCart");
                        boolean hasExisting = false;
                        int existingId = 0;
                        for (int i = 0; i < shoppingCart.size(); i++){
                            if(shoppingCart.get(i).getSKU().equalsIgnoreCase(sku)){
                                quantity = shoppingCart.get(i).getQuantity();
                                existingId = i;
                                hasExisting = true;
                                break;
                            }
                        }
                        
                        if(quantity++ > availableQuantity){
                            response.sendRedirect("/IS3102_Project-war/B/SG/furnitureCategory."
                                + "jsp?cat=" + category 
                                + "&errMsg=Item not added to cart, not enough quantity available. :( " );
                        }
                        else{
                            if(hasExisting){
                                int oldQuantity = shoppingCart.get(existingId).getQuantity();
                                int newQuantity = oldQuantity + 1;
                                shoppingCart.get(existingId).setQuantity(newQuantity);
                                if(request.getParameter("plusbtn") == null){
                                    response.sendRedirect("/IS3102_Project-war/B/SG/furnitureCategory."
                                        + "jsp?cat=" + category
                                        + "&goodMsg=Item added to cart. :)"); 
                                }
                                else{
                                    response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?"
                                        + "goodMsg=Item added to cart. :)"); 
                                }
                            }
                            else{
                                ShoppingCartLineItem item = new ShoppingCartLineItem();
                                item.setId(id);
                                item.setSKU(sku);
                                item.setPrice(price);
                                item.setName(name);
                                item.setImageURL(imageUrl);
                                item.setQuantity(quantity);
                                shoppingCart.add(item);
                                session.setAttribute("shoppingCart", shoppingCart);
                                session.setAttribute("maxQuantity", quantity);
                                category = (String) request.getParameter("category");
                                response.sendRedirect("/IS3102_Project-war/B/SG/furnitureCategory."
                                    + "jsp?cat=" + category
                                    + "&goodMsg=Item added to cart. :)"); 
                            }
                        }
                    }
                }
                catch(Exception e){
                    if(request.getParameter("plusbtn") == null){
                        response.sendRedirect("/IS3102_Project-war/B/SG/furnitureCategory."
                            + "jsp?cat=" + category
                            + "&errMsg=Item could not be added to cart. :("); 
                    }
                    else{
                        response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?"
                            + "&errMsg=Item could not be added to cart. :("); 
                    }
                    e.printStackTrace();
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
