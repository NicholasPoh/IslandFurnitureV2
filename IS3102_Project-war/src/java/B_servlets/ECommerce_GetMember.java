/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package B_servlets;

import HelperClasses.Member;
import HelperClasses.ShoppingCartLineItem;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Author: Koh Ding Yuan
@WebServlet(name = "ECommerce_GetMember", urlPatterns = {"/ECommerce_GetMember"})
public class ECommerce_GetMember extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Client client = ClientBuilder.newClient();
        String email = session.getAttribute("memberEmail").toString();
        int status = 0;
        try{
            WebTarget target = client
                .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.memberentity")
                .path("getMemberInfo")
                .queryParam("email", email);
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response res = invocationBuilder.get();
            status = res.getStatus();
            
            Member user = res.readEntity(new GenericType<Member>(){});
            session.setAttribute("member", user);
            session.setAttribute("memberName", user.getName());
            session.setAttribute("memberPhone", user.getPhone());
            session.setAttribute("memberAddress", user.getAddress());
            session.setAttribute("memberAge", user.getAge());
            session.setAttribute("memberIncome", user.getIncome());
        }
        catch(Exception e){
            System.out.println("Status:" + status);
        }
        
        try{
            WebTarget target = client
                .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.memberentity")
                .path("getMemberInfo")
                .queryParam("email", email);
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response res = invocationBuilder.get();
            status = res.getStatus();
            
            ArrayList<ArrayList<ShoppingCartLineItem>> historySales =
                res.readEntity(new GenericType<ArrayList<ArrayList<ShoppingCartLineItem>>>(){});
            session.setAttribute("historySales", historySales);
        }
        catch(Exception e){
            System.out.println("Status:" + status);
        }
        response.sendRedirect("/IS3102_Project-war/B/SG/memberProfile.jsp");
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
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
