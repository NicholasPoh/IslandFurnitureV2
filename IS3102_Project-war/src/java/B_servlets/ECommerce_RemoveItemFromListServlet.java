package B_servlets;
import HelperClasses.ShoppingCartLineItem;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//Author: Koh Ding Yuan
@WebServlet(name = "ECommerce_RemoveItemFromListServlet", urlPatterns = {"/ECommerce_RemoveItemFromListServlet"})
public class ECommerce_RemoveItemFromListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        try{
            String[] selectedSkus = request.getParameterValues("delete");
            ArrayList<ShoppingCartLineItem> oldCart = (
                ArrayList<ShoppingCartLineItem>)
                session.getAttribute("shoppingCart");
            ArrayList<ShoppingCartLineItem> newCart = new ArrayList<>();
            for (ShoppingCartLineItem item: oldCart){
                boolean add = true;
                for(String selectedSku: selectedSkus){
                    if(selectedSku.equalsIgnoreCase(item.getSKU())){
                        add = false;
                    }
                }
                if(add){
                    newCart.add(item);
                }
            }
            session.setAttribute("shoppingCart", newCart);
            response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?"
                + "goodMsg=Items removed from cart. :)"); 
        }
        catch(Exception e){
            response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?"
                + "&errMsg=Items could not be removed from cart. :("); 
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
