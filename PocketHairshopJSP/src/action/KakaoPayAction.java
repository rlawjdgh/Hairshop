package action;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class KakaoPayAction
 */
@WebServlet("/kakaoPay.do")
public class KakaoPayAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String login_idx = request.getParameter("login_idx");
		String staff_idx = request.getParameter("staff_idx");
		String regdate = request.getParameter("regdate");
		String time = request.getParameter("time");
		String surgery_name = request.getParameter("surgery_name");
		String price = request.getParameter("price");
		
		if(login_idx != null && staff_idx != null && regdate != null && time != null && surgery_name != null && price != null) {
						 
			request.setAttribute("login_idx", login_idx);
			request.setAttribute("staff_idx", staff_idx); 
			request.setAttribute("regdate", regdate);
			request.setAttribute("time", time); 
			request.setAttribute("surgery_name", surgery_name);  
			request.setAttribute("price", price);
			
			RequestDispatcher rd = request.getRequestDispatcher("/kakaoPay.jsp");
			rd.forward(request, response); 
		}
	}
}  
