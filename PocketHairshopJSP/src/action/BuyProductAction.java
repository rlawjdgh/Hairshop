package action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ProductDAO;
import dao.SurgeryDAO;
import vo.ProductVO;
import vo.SurgeryVO;

/**
 * Servlet implementation class BuyProductAction
 */
@WebServlet("/buyProduct.do")
public class BuyProductAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		
		String login_idx = request.getParameter("login_idx"); 
		
		if(login_idx != null) {
			
			ProductDAO pDao = ProductDAO.getInstance();
			SurgeryDAO sDao = SurgeryDAO.getInstance();
			List<ProductVO> pList = pDao.getBuyProduct(Integer.parseInt(login_idx)); 
			
			String arr = "[";
			
			for(int i = 0; i < pList.size(); i++) {
				
				List<SurgeryVO> sList = sDao.getBuyProduct(pList.get(i).getSurgery_idx());
				
				for(int j = 0; j < sList.size(); j++) {
					
					String str = String.format("{'name':'%s', 'photo':'%s', 'regdate':'%s'}", sList.get(i).getName(), sList.get(i).getPhoto(), pList.get(i).getRegdate());
					arr += str; 
					
					if(j != pList.size() -1) {  
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
