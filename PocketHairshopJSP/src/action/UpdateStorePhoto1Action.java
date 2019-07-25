package action;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.StoreDAO;
import vo.StoreVO;

@WebServlet("/update_storephoto1.do")
public class UpdateStorePhoto1Action extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		
		String file_name = request.getParameter("file_name");
		String idx = request.getParameter("nickName_idx");
		
		String web_path = "/store_photo/";
		
		//현재 WebApplication을 관리하는 객체 ServletContext
		ServletContext application = request.getServletContext();
		String real_path = application.getRealPath( web_path );
		
		
		if( file_name != null && idx != null ) {
			
			int nickName_idx = Integer.parseInt( idx );
			
			StoreDAO dao = StoreDAO.getInstance();
			
			StoreVO original_vo = dao.selectOneStore_nickNameIdx(nickName_idx);  
			String original_photo1 = original_vo.getPhoto1();
			
			File photo1 = new File(real_path + "/" + original_photo1);
			
			if( photo1.exists() ) {
				photo1.delete();
			}
			
			StoreVO vo = new StoreVO(); 
			vo.setNickName_idx(nickName_idx);
			vo.setPhoto1(file_name);
			
			int result = dao.updatePhoto1( vo );
			String resultStr = "fail";
			
			if( result >= 1 ) {
				resultStr = "success";
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
			
		}
		
	}

}
