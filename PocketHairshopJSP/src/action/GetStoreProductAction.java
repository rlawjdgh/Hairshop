package action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.SurgeryDAO;
import vo.SurgeryVO;

/**
 * Servlet implementation class GetStoreProductAction
 */
@WebServlet("/getStoreProduct.do")
public class GetStoreProductAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		request.setCharacterEncoding("utf-8"); 
		
		String store_idx = request.getParameter("store_idx");
		
		if(store_idx != null) {
			
			SurgeryDAO dao = SurgeryDAO.getInstance();
			SurgeryVO vo = new SurgeryVO();
			vo.setNickName_idx(Integer.parseInt(store_idx));
			vo.setCategory(4);
			
			List<SurgeryVO> list = dao.getItemSurgery(vo); 
			
			String arr = "[";
			 
			for(int i = 0 ; i < list.size(); i++) {
				SurgeryVO json = list.get(i); 
				
				String str = String.format("{'surgery_idx':'%d', 'name':'%s', 'price':'%d', 'photo':'%s'}", json.getSurgery_idx(), json.getName(), json.getPrice(), json.getPhoto());
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
