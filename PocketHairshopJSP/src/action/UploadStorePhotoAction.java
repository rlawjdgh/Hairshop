package action;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

@WebServlet("/upload_storephoto.do")
public class UploadStorePhotoAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String web_path = "/store_photo/";
		
		//현재 WebApplication을 관리하는 객체 ServletContext
		ServletContext application = request.getServletContext();
		String real_path = application.getRealPath( web_path ); 
		
		int max_size = 1024 * 1024 * 100; //최대 업로드 용량 : 100MB
		
		MultipartRequest mr = new MultipartRequest( request, real_path, max_size, "utf-8", new DefaultFileRenamePolicy() );
		String file_name = mr.getFilesystemName("uploaded_file");
		 
		String result = String.format("[{'result':'success', 'file_name':'%s'}]", file_name);
		response.getWriter().println(result);
		
	} 

}
