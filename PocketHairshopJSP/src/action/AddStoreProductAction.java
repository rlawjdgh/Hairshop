package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ProductDAO;
import vo.ProductVO;

/**
 * Servlet implementation class AddStoreProductAction
 */
@WebServlet("/addStoreProduct.do")
public class AddStoreProductAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String resultStr = "";
		String login_idx = request.getParameter("login_idx");
		String surgery_idx = request.getParameter("surgery_idx");
		
		if(login_idx != null && surgery_idx != null) {
			
			ProductDAO dao = ProductDAO.getInstance();
			ProductVO vo = new ProductVO();
			
			vo.setLogin_idx(Integer.parseInt(login_idx));
			vo.setSurgery_idx(Integer.parseInt(surgery_idx));
			
			int result = dao.addStoreProduct(vo); 
			 
			if( result >= 1 ) {
				resultStr = "success"; 
			}else {
				resultStr = "fail";
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
		}
	}

}
