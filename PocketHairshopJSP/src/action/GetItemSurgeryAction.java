package action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.SurgeryDAO;
import vo.SurgeryVO;

/**
 * Servlet implementation class GetItemSurgeryAction
 */
@WebServlet("/getItemSurgery.do")
public class GetItemSurgeryAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String nickName_idx = request.getParameter("nickName_idx");
		String category = request.getParameter("category");
		
		if(nickName_idx != null && category != null) {
			
			SurgeryVO vo = new SurgeryVO();
			vo.setNickName_idx(Integer.parseInt(nickName_idx));
			vo.setCategory(Integer.parseInt(category));
			
			SurgeryDAO dao = SurgeryDAO.getInstance();
			List<SurgeryVO> list = dao.getItemSurgery(vo);
				
				String arr = "[";
				 
				for(int i = 0 ; i < list.size(); i++) {
					SurgeryVO json = list.get(i); 
					
					String str = String.format("{'surgery_idx':'%d', 'category':'%d', 'name':'%s', 'price':'%d', 'photo':'%s'}", json.getSurgery_idx(), json.getCategory(), json.getName(), json.getPrice(), json.getPhoto());
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
