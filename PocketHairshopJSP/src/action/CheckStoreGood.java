package action;

import java.io.IOException;
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
 * Servlet implementation class CheckStoreGood
 */
@WebServlet("/storeGood.do")
public class CheckStoreGood extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");

		int result;
		String resultStr = "";
		int good;

		String num = request.getParameter("number");
		String l_idx = request.getParameter("login_idx");
		String s_idx = request.getParameter("store_idx");

		if (num != null && l_idx != null && s_idx != null) {

			int number = Integer.parseInt(num);
			int login_idx = Integer.parseInt(l_idx);
			int store_idx = Integer.parseInt(s_idx);

			GoodDAO gDAO = GoodDAO.getInstance();
			GoodVO gVO = new GoodVO();
			gVO.setLogin_idx(login_idx);
			gVO.setStore_idx(store_idx); 
			
			GoodVO gVO2 = gDAO.checkStoreGood(gVO); 
			
			StoreDAO sDAO = StoreDAO.getInstance();	
			StoreVO sVO = sDAO.getStoreInfo(store_idx);
			StoreVO sVO2 = new StoreVO();
			
			sVO2.setNickName_idx(store_idx); 
			 
			if (gVO2 == null) {  
			 
				if(number == 1) {
					gDAO.insertStoreGood(gVO);
		
					good = sVO.getGood() + 1;
					sVO2.setGood(good); 
					
					sDAO.updateGood(sVO2); 
				} 
				resultStr = "insert"; 
				
			} else { 
				  
				if(number == 1) { 
					gDAO.removeStoreGood(gVO); 
					
					good = sVO.getGood() - 1;
					if(good <= -1) { 
						good = 0;  
					}
					sVO2.setGood(good); 
					
					//sDAO.removeGood(sVO2); 
					sDAO.updateGood(sVO2);   
				}  
				resultStr = "remove"; 
			}   

			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
		}
	}
}
