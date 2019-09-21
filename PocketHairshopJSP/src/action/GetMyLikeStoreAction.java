package action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.GoodDAO;
import dao.StoreDAO;
import vo.GoodVO;
import vo.StoreVO;

/**
 * Servlet implementation class GetMyLikeStoreAction
 */
@WebServlet("/getMyLikeStore.do")
public class GetMyLikeStoreAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String login_idx = request.getParameter("login_idx");
		
		if(login_idx != null) {
			
			GoodDAO gDao = GoodDAO.getInstance();
			StoreDAO sDao = StoreDAO.getInstance();
			List<GoodVO> gList = gDao.findMyLikeStore(Integer.parseInt(login_idx)); 
			
			String arr = "[";
			
			for(int i = 0; i < gList.size(); i++) {
				
				List<StoreVO> sList = sDao.findStoreName(gList.get(i).getStore_idx());
				
				for(int j = 0; j < sList.size(); j++) {
					
					String str = String.format("{'nickName_idx':'%d', 'name':'%s', 'photo1':'%s', 'info':'%s', 'good':'%s'}", sList.get(j).getNickName_idx(), sList.get(j).getName(), sList.get(j).getPhoto1(), sList.get(j).getInfo(), sList.get(j).getGood());
					arr += str; 
					
					if(j != gList.size() -1) {  
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
 