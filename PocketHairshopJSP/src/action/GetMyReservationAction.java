package action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.MemberDAO;
import dao.ReservationDAO;
import dao.StaffDAO;
import dao.StoreDAO;
import vo.ReservationVO;
import vo.StaffVO;
import vo.StoreVO;

/**
 * Servlet implementation class GetMyReservationAction
 */
@WebServlet("/getMyReservation.do")
public class GetMyReservationAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");  
		
		String login_idx = request.getParameter("login_idx");   
		
		if(login_idx != null) { 
			
			ReservationDAO rDao = ReservationDAO.getInstance();
			StaffDAO sDao = StaffDAO.getInstance();
			StoreDAO tDao = StoreDAO.getInstance();
			
			List<ReservationVO> rList = rDao.getMyReservation(Integer.parseInt(login_idx));
			
			String arr = "["; 
			
			for(int i = 0; i < rList.size(); i++) {
				
				List<StaffVO> sList = sDao.findStaffName(rList.get(i).getStaff_idx());
				List<StoreVO> tList = tDao.findStoreName(rList.get(i).getStore_idx()); 
				
				for(int j = 0; j < sList.size(); j++) {
					 
					String str = String.format("{'reservation_idx':'%d', 'store_idx':'%d', 'store_name':'%s', 'staff_name':'%s', 'staff_grade':'%s', 'cal_day':'%s', 'getTime':'%s', 'surgery_name':'%s', 'complete':'%d'}", rList.get(i).getReservation_idx(), rList.get(i).getStore_idx(), tList.get(j).getName(), sList.get(j).getName(), sList.get(j).getGrade(), rList.get(i).getCal_day(), rList.get(i).getGetTime(), rList.get(i).getSurgery_name(), rList.get(i).getComplete());
					arr += str;    
					   
					if(j != rList.size() -1) {   
						arr += ", ";    
					}  
				}
			}
			  
			arr += "]"; 
			 
			response.setCharacterEncoding("UTF-8");  
			response.setContentType("text/html; charset=UTF-8"); 
			response.getWriter().println(arr);
		}
	}

}
