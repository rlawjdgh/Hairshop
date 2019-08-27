package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ReservationDAO;
import vo.ReservationVO;

/**
 * Servlet implementation class InsertReservationAction
 */
@WebServlet("/insertReservation.do")
public class InsertReservationAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
	
		String resultStr = "";
		
		String login_idx = request.getParameter("login_idx");
		String staff_idx = request.getParameter("staff_idx");
		String cal_day = request.getParameter("cal_day"); 
		String getTime = request.getParameter("getTime");
		String surgery_name = request.getParameter("surgery_name");
		String price = request.getParameter("price");
		 
		System.out.println("login_idx : " + login_idx + "staff_idx : " + staff_idx + "cal_day : " + cal_day + "getTime : " + getTime + "surgery_name : " + surgery_name + "price : " + price);
		 
		if(login_idx != null && staff_idx != null && cal_day != null && getTime != null && surgery_name != null && price != null) {
			
			ReservationVO vo = new ReservationVO();
			
			vo.setLogin_idx(Integer.parseInt(login_idx));
			vo.setStaff_idx(Integer.parseInt(staff_idx));
			vo.setCal_day(cal_day);
			vo.setGetTime(getTime);
			vo.setSurgery_name(surgery_name);
			vo.setPrice(Integer.parseInt(price)); 
			
			ReservationDAO dao = ReservationDAO.getInstance();
			int result = dao.insertReservation(vo);
			
			if( result >= 1 ) {
				resultStr = "success"; 
			}else { 
				resultStr = "fail"; 
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));	
		}
	} 
}
