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
import vo.MemberVO;
import vo.ReservationVO;
import vo.StaffVO;

/**
 * Servlet implementation class GetReservationAction
 */
@WebServlet("/getReservation.do")
public class GetReservationAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String store_idx = request.getParameter("store_idx"); 
		
		if(store_idx != null) {
			
			ReservationDAO rDao = ReservationDAO.getInstance();
			StaffDAO sDao = StaffDAO.getInstance();
			MemberDAO mDao = MemberDAO.getInstance();  
			
			List<ReservationVO> rList = rDao.selectReservation(Integer.parseInt(store_idx)); 
			 
			String arr = "["; 
			
			for(int i = 0; i < rList.size(); i++) {
				 
				List<StaffVO> sList = sDao.findStaffName(rList.get(i).getStaff_idx());  
				List<MemberVO> mList = mDao.findName(rList.get(i).getLogin_idx());  
				
				for(int j = 0; j < sList.size(); j++) { 
					  
					String str = String.format("{'reservation_idx':'%d', 'user_nickName':'%s', 'staff_name':'%s', 'staff_grade':'%s', 'cal_day':'%s', 'getTime':'%s', 'surgery_name':'%s', 'regdate':'%s', 'complete':'%d'}", rList.get(i).getReservation_idx(), mList.get(j).getNickName(), sList.get(j).getName(), sList.get(j).getGrade(), rList.get(i).getCal_day(), rList.get(i).getGetTime(), rList.get(i).getSurgery_name(), rList.get(i).getRegdate() ,rList.get(i).getComplete());
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
