package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ReservationDAO;

/**
 * Servlet implementation class ReservationCompleteAction
 */
@WebServlet("/reservationComplete.do")
public class ReservationCompleteAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String reservation_idx = request.getParameter("reservation_idx");
		
		if(reservation_idx != null) {
			
			String resultStr = "";
			
			ReservationDAO dao = ReservationDAO.getInstance();
			int result = dao.reservationComplete(Integer.parseInt(reservation_idx));
			 
			if( result >= 1 ) {
				resultStr = "success"; 
			}else {
				resultStr = "fail"; 
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
		}
	}

}
