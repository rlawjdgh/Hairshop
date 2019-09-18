package action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.StoreDAO;
import vo.StoreVO;

/**
 * Servlet implementation class GetSearchStoreAction
 */
@WebServlet("/getSearchStore.do")
public class GetSearchStoreAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String search = request.getParameter("search");
		
		if(search != null) {
			
			StoreDAO dao = StoreDAO.getInstance();
			List<StoreVO> list = dao.searchStore(search);
			
			String arr = "[";
			
			for(int i = 0; i < list.size(); i++) {
						
				String str = String.format("{'nickName_idx':'%d', 'name':'%s', 'photo1':'%s', 'info':'%s', 'good':'%s'}", list.get(i).getNickName_idx(), list.get(i).getName(), list.get(i).getPhoto1(), list.get(i).getInfo(), list.get(i).getGood());
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
