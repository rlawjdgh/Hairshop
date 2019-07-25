package action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.StaffDAO;
import dao.SurgeryDAO;
import vo.StaffVO;
import vo.SurgeryVO;

/**
 * Servlet implementation class GetItemSurgeryAction
 */
@WebServlet("/getItemStaff.do") 
public class GetItemStaffAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String nickName_idx = request.getParameter("nickName_idx");
		
		if(nickName_idx != null) {
			
			StaffDAO dao = StaffDAO.getInstance();
			List<StaffVO> list = dao.getItemStaff(Integer.parseInt(nickName_idx)); 
				 
				String arr = "[";
				 
				for(int i = 0 ; i < list.size(); i++) {
					StaffVO json = list.get(i); 
					
					System.out.println(json.getStaff_idx()); 
					 
					String str = String.format("{'staff_idx':'%d', 'nickName_idx':'%d', 'name':'%s', 'info':'%s', 'grade':'%s', 'photo':'%s'}", json.getStaff_idx(), json.getNickName_idx(), json.getName(), json.getInfo(), json.getGrade(), json.getPhoto());
					arr += str;
					 
					if(i != list.size() -1) {  
						arr += ", ";  
					}
				}   
				
				arr += "]";
				
				System.out.println(arr);
				response.setCharacterEncoding("UTF-8");  
				response.setContentType("text/html; charset=UTF-8"); 
				response.getWriter().println(arr);   
		}
	} 

}
