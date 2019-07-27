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
 * Servlet implementation class getStoreAllAction
 */
@WebServlet("/getStoreAll.do")
public class getStoreAllAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int nickName_idx;
		String name;
		String address1;
		String address2;
		String photo1;
		String info;
		int good;
		
		StoreDAO dao = StoreDAO.getInstance();
		List<StoreVO> list = dao.getStoreAll();
		
		String arr = "[";
		for(int i = 0 ; i < list.size(); i++) {
			
			nickName_idx = list.get(i).getNickName_idx();
			name = list.get(i).getName();
			address1 = list.get(i).getAddress1();
			address2 = list.get(i).getAddress2();
			photo1 = list.get(i).getPhoto1();
			info = list.get(i).getInfo();
			good = list.get(i).getGood();
			
			String str = String.format("{'nickName_idx':'%d', 'name':'%s', 'address1':'%s', 'photo1':'%s', 'info':'%s', 'good':'%s'}", nickName_idx, name, address1, photo1, info, good);
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
