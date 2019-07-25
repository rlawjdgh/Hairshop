package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.StaffDAO;
import vo.StaffVO;

/**
 * Servlet implementation class InsertStaffAction
 */
@WebServlet("/insertStaff.do")
public class InsertStaffAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8"); 
		
		String idx = request.getParameter("nickName_idx");
		String name = request.getParameter("name");
		String info = request.getParameter("info");
		String grade = request.getParameter("grade");
		
		if(idx != null && name != null && info != null && grade != null) {
			
			String resultStr = "";
			int nickName_idx = Integer.parseInt(idx);
		
			StaffVO vo = new StaffVO();
			
			vo.setNickName_idx(nickName_idx);
			vo.setName(name);
			vo.setInfo(info); 
			vo.setGrade(grade);
			
			StaffDAO dao = StaffDAO.getInstance();
			int result = dao.insertStaff(vo);
			
			if( result >= 1 ) { 
				resultStr = "success"; 
			}else {
				resultStr = "fail"; 
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
			
		}
	}

}
