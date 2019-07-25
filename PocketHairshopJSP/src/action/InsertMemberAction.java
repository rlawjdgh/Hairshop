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
 * Servlet implementation class InsertMemberAction
 */
@WebServlet("/insertMember.do")
public class InsertMemberAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("utf-8"); 
		
		String resultStr = "";
 
		String idx = request.getParameter("idx");
		String nickName = request.getParameter("nickName"); 
		String email = request.getParameter("email");
		int division = Integer.parseInt(request.getParameter("division"));

		if (idx != null && nickName != null && email != null) {

			MemberVO vo = new MemberVO();

			vo.setIdx(Integer.parseInt(idx));
			vo.setNickName(nickName);
			vo.setEmail(email);
			vo.setDivision(division);

			MemberDAO dao = MemberDAO.getInstance();
			int result = dao.insertMember(vo);
			
			if( result >= 1 ) {
				resultStr = "success";
			}else {
				resultStr = "fail";
			}
			
			response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
		}
		
	}  
	
}
