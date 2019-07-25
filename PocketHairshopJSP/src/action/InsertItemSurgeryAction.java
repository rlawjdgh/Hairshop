package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.SurgeryDAO;
import vo.SurgeryVO;

/**
 * Servlet implementation class InsertItemSurgeryAction
 */
@WebServlet("/insertItemSurgery.do")
public class InsertItemSurgeryAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		
		String idx = request.getParameter("nickName_idx");
		String number = request.getParameter("category");
		String name = request.getParameter("name");
		String gabs = request.getParameter("price");
		
		if(idx != null && number != null && name != null && gabs != null) {
			
			String resultStr = "";
			int nickName_idx = Integer.parseInt(idx);
			int category = Integer.parseInt(number);
			int price = Integer.parseInt(gabs);
			
			SurgeryVO vo = new SurgeryVO();
			
			vo.setNickName_idx(nickName_idx);
			vo.setCategory(category);
			vo.setName(name);
			vo.setPrice(price);
			
			SurgeryDAO dao = SurgeryDAO.getInstance();
			int result = dao.insertItemSurgery(vo);
			
			if( result >= 1 ) {
				resultStr = "success"; 
			}else {
				resultStr = "fail"; 
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
		}
		
	}
 
}
