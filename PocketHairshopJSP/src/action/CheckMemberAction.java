package action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.MemberDAO;
import vo.MemberVO;

/**
 * Servlet implementation class CheckMemberAction
 */
@WebServlet("/checkMember.do") 
public class CheckMemberAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		request.setCharacterEncoding("utf-8"); 
		 
		String idx = request.getParameter("idx");
			 
		if(idx != null) {
			 
			MemberDAO dao = MemberDAO.getInstance();
			MemberVO vo = dao.selectMember(Integer.parseInt(idx)); 
			  
			if( vo == null ) {
				response.getWriter().println(String.format("[{'result':'fail'}]")); 
			}else {  
				String str = String.format("[{'idx':'%d', 'nickName':'%s', 'email':'%s', 'division':'%d', 'result':'success'}]", vo.getIdx(), vo.getNickName(), vo.getEmail(), vo.getDivision());
				 
				response.setCharacterEncoding("UTF-8"); 
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().println(str);
			}
		}
	}
} 
