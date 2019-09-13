package action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ReplyDAO;
import dao.ReviewDAO;
import vo.ReplyVO;

/**
 * Servlet implementation class InsertReplyAction
 */
@WebServlet("/insertReply.do")
public class InsertReplyAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");

		String resultStr = "";
		String review_idx = request.getParameter("review_idx");
		String context = request.getParameter("context"); 
 
		if (review_idx != null && context != null) { 

			ReviewDAO dao = ReviewDAO.getInstance();
			
			int complete = dao.updateReviewComplete(Integer.parseInt(review_idx));
			String staff_name = dao.findStaffName(Integer.parseInt(review_idx));

			if (complete >= 1) {

				ReplyDAO reDao = ReplyDAO.getInstance();
				ReplyVO vo = new ReplyVO();
				
				vo.setReview_idx(Integer.parseInt(review_idx));
				vo.setStaff_name(staff_name); 
				vo.setContext(context);

				int result = reDao.insertReply(vo);

				if (result >= 1) {
					resultStr = "success";
				} else {
					resultStr = "fail";
				}
 
				response.getWriter().println(String.format("[{'result':'%s'}]", resultStr));
			}
		}
	}

}
