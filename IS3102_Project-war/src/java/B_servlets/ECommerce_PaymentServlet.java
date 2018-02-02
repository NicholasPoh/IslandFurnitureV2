package B_servlets;
import HelperClasses.Member;
import HelperClasses.ShoppingCartLineItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Author: Koh Ding Yuan
@WebServlet(name = "ECommerce_PaymentServlet", urlPatterns = {"/ECommerce_PaymentServlet"})
public class ECommerce_PaymentServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("member");
        String memberId =  "8351";//member.getId() + "";
        String storeId = "59";
        String transactionAmount = session.getAttribute("totalPrice") + "";
        ArrayList<ShoppingCartLineItem> shoppingCart = 
            (ArrayList<ShoppingCartLineItem>) session.getAttribute("shoppingCart");
        
        Client client = ClientBuilder.newClient();
        Form data = new Form();
        data.param("memberId", memberId);
        data.param("storeId", storeId);
        data.param("transactionAmount", transactionAmount);
        
        int srId = 0, checkPoint = 0;
        
        //Create Sales Record
        try{
           WebTarget target = client
                .target("http://localhost:8080/IS3102_WebService-Student/webresources/commerce")
                .path("createSalesRecord");
               
            Invocation.Builder invocationBuilder = target.request();
            Response res = invocationBuilder.post(
                Entity.entity(data, "application/x-www-form-urlencoded"));
            
            if(res.getStatus() == 200){
                srId = Integer.parseInt(res.readEntity(String.class));
                checkPoint++;
            }
            System.out.println(res.getStatus());
            System.out.println("create sales done");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        //Create Line Item Record
        if(checkPoint == 1){
            for(ShoppingCartLineItem item: shoppingCart){
                try{
                    int itemId = Integer.parseInt(item.getId());
                    System.out.println(itemId);
                    int itemQuantity = item.getQuantity();
                    WebTarget target = client
                        .target("http://localhost:8080/IS3102_WebService-Student/webresources/commerce")
                        .path("createLineItemRecord")
                        .queryParam("srId", srId)
                        .queryParam("itemId", itemId)
                        .queryParam("itemQuantity", itemQuantity);
                    Invocation.Builder invocationBuilder = target.request();
                    Response res = invocationBuilder.post(
                        Entity.entity(data, "application/json"));

                    if(res.getStatus() != 200){
                        checkPoint = -1000;
                        System.out.println(res.getStatus());
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    checkPoint = -1000;
                    break;
                }
            }
            checkPoint++;
            System.out.println("lineitem Done");
        }
        
        //Update Stock
        if(checkPoint == 2){
            for(ShoppingCartLineItem item: shoppingCart){
                try{
                    int itemId = Integer.parseInt(item.getId());
                    int itemQuantity = item.getQuantity();
                    WebTarget target = client
                        .target("http://localhost:8080/IS3102_WebService-Student/webresources/commerce")
                        .path("stockUpdate")
                        .queryParam("itemId", itemId)
                        .queryParam("itemQuantity", itemQuantity)
                        .queryParam("itemName", item.getName());
                    Invocation.Builder invocationBuilder = target.request();
                    Response res = invocationBuilder.post(
                        Entity.entity(data, "application/json"));

                    if(res.getStatus() != 200){
                        checkPoint = -1000;
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    checkPoint = -1000;
                    break;
                }
            }
            checkPoint++;
            System.out.println("stockdone");
        }
        System.out.println(checkPoint);
        if(checkPoint == 3){
            ArrayList<ShoppingCartLineItem> newCart = new ArrayList<>();
            session.setAttribute("shoppingCart", newCart);
            response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart."
                + "jsp?goodMsg=Check out successful. The items will be delievered to you within"
                + " a week. ):"); 
        }
        else{
            response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart."
                + "jsp?errMsg=Check out failed. :("); 
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
