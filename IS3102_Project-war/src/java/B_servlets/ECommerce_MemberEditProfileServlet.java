/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package B_servlets;

import HelperClasses.Member;
import java.io.IOException;
import java.io.PrintWriter;
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
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Author: Koh Ding Yuan
@WebServlet(name = "ECommerce_MemberEditProfileServlet", urlPatterns = {"/ECommerce_MemberEditProfileServlet"})
public class ECommerce_MemberEditProfileServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        int denyUpdate = 0;
        //long id = member.getId();
        
        String name, phone, country, address, newPw, newPwConfirm, securityAns;
        name = phone = country = address = newPw = newPwConfirm = securityAns = "";
        int securityQues, age, income;
        securityQues = age = income = 0;
        
        try{
            Member member = (Member) session.getAttribute("member");
            name = request.getParameter("name");
            phone = request.getParameter("phone");
            country = request.getParameter("country");
            address = request.getParameter("address");
            securityQues = Integer.parseInt(request.getParameter("securityQuestion"));
            securityAns = request.getParameter("securityAnswer");
            age = Integer.parseInt(request.getParameter("age"));
            income = Integer.parseInt(request.getParameter("income"));
        }
        catch(Exception a){
            denyUpdate = 1;
            //Error in retriving input data
        }
        
        try{
            newPw = request.getParameter("password");
            if(newPw.equals("")){
                newPwConfirm = request.getParameter("repassword");
                if(!newPw.equals(newPwConfirm)){
                    denyUpdate = 1;
                }
            }
        }
        catch(Exception b){
            denyUpdate = 1;
            
            //Error in password confirmation
        }
        
        int memberId = 0; boolean hasMember = false;
        if(denyUpdate == 0){
            Client client = ClientBuilder.newClient();
            String email = session.getAttribute("memberEmail").toString();
            int status = 0;
            try{
                WebTarget target = client
                    .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.memberentity")
                        .path("updateMemberInfo")
                        .queryParam("email", email)
                        .queryParam("name", name)
                        .queryParam("phone", phone)
                        .queryParam("country" ,country)
                        .queryParam("address" ,address)
                        .queryParam("securityQues" ,securityQues)
                        .queryParam("securityAns", securityAns)
                        .queryParam("age", age)
                        .queryParam("income", income)
                        .queryParam("password", newPw);
                Invocation.Builder invocationBuilder = target.request();
                Response res = invocationBuilder.put(Entity.entity("", "application/json"));
                status = res.getStatus();
                Member user = res.readEntity(new GenericType<Member>(){});
                
                session.setAttribute("member", user);
                session.setAttribute("memberName", user.getName());
                session.setAttribute("memberPhone", user.getPhone());
                session.setAttribute("memberAddress", user.getAddress());
                session.setAttribute("memberAge", user.getAge());
                session.setAttribute("memberIncome", user.getIncome());
                memberId = Math.toIntExact(user.getId());
                
                response.sendRedirect("/IS3102_Project-war/B/SG/memberProfile.jsp?goodMsg=Account updated successfully");
                hasMember = true;
            }
            catch(Exception e){
                System.out.println("Status:" + status);
            }
            
            if(hasMember){
                WebTarget target = client
                    .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.memberentity")
                        .path("getMemberSalesHistory")
                        .queryParam("memberId", memberId);
                Invocation.Builder invocationBuilder = target.request();
                Response res = invocationBuilder.put(Entity.entity("", "application/json"));
                status = res.getStatus();
                
                
            }
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
