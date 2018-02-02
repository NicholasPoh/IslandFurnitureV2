package B_servlets;
import HelperClasses.ShoppingCartLineItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

//Author: Koh Ding Yuan
@WebServlet(name = "ECommerce_MinusFurnitureToListServlet", urlPatterns = {"/ECommerce_MinusFurnitureToListServlet"})
public class ECommerce_MinusFurnitureToListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String sku = request.getParameter("SKU");
        try {
            ArrayList<ShoppingCartLineItem> shoppingCart ;
            shoppingCart = (ArrayList<ShoppingCartLineItem>)
                session.getAttribute("shoppingCart");
            int existingId = 0; int oldQuantity = 0; int newQuantity = 0;
            for (int i = 0; i < shoppingCart.size(); i++){
                if(shoppingCart.get(i).getSKU().equalsIgnoreCase(sku)){
                    oldQuantity = shoppingCart.get(i).getQuantity();
                    existingId = i;
                    break;
                }
            }
            
            oldQuantity = shoppingCart.get(existingId).getQuantity();
            newQuantity = oldQuantity - 1;
            
            if(newQuantity == 0){
                shoppingCart.remove(existingId);
            }
            else{
                shoppingCart.get(existingId).setQuantity(newQuantity);
            }
            response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?"
                + "goodMsg=Item removed from cart. :)"); 
        }
        catch(Exception e){
            response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?"
                + "&errMsg=Item could not be removed from cart. :(");
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
