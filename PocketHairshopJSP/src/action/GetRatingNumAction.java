package action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ReservationDAO;
import dao.ReviewDAO;
import vo.ReservationVO;
import vo.ReviewVO;

/**
 * Servlet implementation class GetRatingNumAction
 */
@WebServlet("/getRatingNum.do")
public class GetRatingNumAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String staff_idx = request.getParameter("staff_idx");
		
		if(staff_idx != null) {
			
			ReservationDAO rDao = ReservationDAO.getInstance();
			ReviewDAO reDao = ReviewDAO.getInstance();
			List<ReservationVO> rList = rDao.findReview(Integer.parseInt(staff_idx)); 
			
			String arr = "[";
			for(int i = 0; i < rList.size(); i++) {
				
				List<ReviewVO> reList = reDao.findRating(rList.get(i).getReservation_idx());
				
				for(int j = 0; j < reList.size(); j++) {
					
					String str = String.format("{'rating':'%d'}", reList.get(j).getRating());
					arr += str;
					 
					if(i != rList.size() -1) {  
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
