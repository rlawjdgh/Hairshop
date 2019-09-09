package action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ReservationDAO;
import vo.ReservationVO;
import vo.SurgeryVO;

/**
 * Servlet implementation class GetReservationTime
 */ 
@WebServlet("/getReservationTime.do")
public class GetReservationTime extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String cal_day = request.getParameter("cal_day");
		
		if(cal_day != null) {
			
			ReservationDAO dao = ReservationDAO.getInstance();
			List<ReservationVO> list = dao.getReservationTime(cal_day);
			
			String arr = "[";
			 
			for(int i = 0 ; i < list.size(); i++) {
				
				String str = String.format("{'getTime':'%s'}", list.get(i).getGetTime());
				arr += str; 
				
				if(i != list.size() -1) {   
					arr += ", ";  
				}
			}   
			
			arr += "]"; 

			response.setCharacterEncoding("UTF-8");  
			response.setContentType("text/html; charset=UTF-8"); 
			response.getWriter().println(arr);   
		}
	}

}
