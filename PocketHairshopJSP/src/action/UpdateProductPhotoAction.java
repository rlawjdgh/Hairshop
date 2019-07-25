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
import dao.SurgeryDAO;
import vo.StoreVO;
import vo.SurgeryVO;

@WebServlet("/update_ProductPhoto.do")
public class UpdateProductPhotoAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		
		String file_name = request.getParameter("file_name");
		String idx = request.getParameter("nickName_idx"); 
		String number = request.getParameter("category");
		String name = request.getParameter("name");
	 
		String web_path = "/product_photo/"; 
		
		//현재 WebApplication을 관리하는 객체 ServletContext
		ServletContext application = request.getServletContext();
		String real_path = application.getRealPath( web_path );
		
		
		if( file_name != null && idx != null && number != null && name != null) { 
			
			int nickName_idx = Integer.parseInt( idx );
			int category = Integer.parseInt(number);		
			
			SurgeryDAO dao = SurgeryDAO.getInstance();
			
			SurgeryVO vo = new SurgeryVO(); 
			vo.setNickName_idx(nickName_idx);
			vo.setCategory(category);
			vo.setName(name);
			vo.setPhoto(file_name); 
			
			int result = dao.updatePhoto(vo);
			String resultStr = "fail";
			
			if( result >= 1 ) {
				resultStr = "success";
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
			
		}
		
	}

}
