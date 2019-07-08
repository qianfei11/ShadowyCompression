package client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import supplement.Paillier;
import supplement.PublicKey;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	static int width;
	static int height;
	static int quality_scale;
	static int[][] Images;
	static int[][][] quanArr;
	static String filePath;
	static String outputPath;

	static String server_pk_ip;
	static String server_pk_port;
	static String server_sk_ip;
	static String server_sk_port;
	static String client_ip;
	static String client_port;

	static PublicKey PK;
	static BigInteger sk;
	static Paillier p;

	private static final long serialVersionUID = 1L;

	// 上传文件存储目录
	private static final String UPLOAD_DIRECTORY = "upload";

	// 上传配置
	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	/**
	 * 上传数据及保存文件
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 检测是否为多媒体上传
		if (!ServletFileUpload.isMultipartContent(request)) {
			// 如果不是则停止
			PrintWriter writer = response.getWriter();
			writer.println("Error: Post data must have enctype=multipart/form-data");
			writer.flush();
			return;
		}

		// 配置上传参数
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
		factory.setSizeThreshold(MEMORY_THRESHOLD);
		// 设置临时存储目录
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);

		// 设置最大文件上传值
		upload.setFileSizeMax(MAX_FILE_SIZE);

		// 设置最大请求值 (包含文件和表单数据)
		upload.setSizeMax(MAX_REQUEST_SIZE);

		// 中文处理
		upload.setHeaderEncoding("UTF-8");

		// 构造临时路径来存储上传的文件
		// 这个路径相对当前应用的目录
		String uploadPath = getServletContext().getRealPath("/") + File.separator + UPLOAD_DIRECTORY;

		// 如果目录不存在则创建
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		try {
			// 解析请求的内容提取文件数据
			List<FileItem> formItems = upload.parseRequest(request);

			if (formItems != null && formItems.size() > 0) {
				// 迭代表单数据
				for (FileItem item : formItems) {
					// 处理不在表单中的字段
					if (!item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						filePath = uploadPath + File.separator + fileName;
						outputPath = uploadPath + File.separator + fileName.replaceAll("bmp", "jpg");
						request.setAttribute("result", fileName.replaceAll("bmp", "jpg"));
						File storeFile = new File(filePath);
						// 在控制台输出文件的上传路径
						System.out.println("Upload Path: " + filePath);
						// 保存文件到硬盘
						item.write(storeFile);
						request.setAttribute("message", "File was uploaded successfully!");
					}
				}
			}
		} catch (Exception ex) {
			request.setAttribute("message", "Error: " + ex.getMessage());
		}

		// Compression
		server_pk_ip = "127.0.0.1";
		server_pk_port = "44444";
		server_sk_ip = "127.0.0.1";
		server_sk_port = "55555";
		client_ip = "127.0.0.1";
		client_port = "33333";
		quality_scale = 50;
		p = new Paillier();
		PK = p.KeyGen(1024, 64);
		sk = p.get_sk();
		long startTime = System.currentTimeMillis();
		try {
			getImage(filePath);
			Upload.upload(server_pk_ip, server_pk_port, server_sk_ip, server_sk_port, PK, sk, Images);
			quanArr = Receive.receiveQuantificationArray(PK, sk, client_ip, client_port);

			long huffmanStartTime = System.currentTimeMillis();
			System.out.println("[*] Start Canonical Huffman encode...");
			Huffman.CanonicalHuffman(quanArr, outputPath, width, height, quality_scale);
			System.out.println("[*] Canonical Huffman encoded successfully!");
			long huffmanEndTime = System.currentTimeMillis();
			System.out.println("[*] Canonical Huffman takes " + (huffmanEndTime - huffmanStartTime) + "ms");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("[*] Total Time: " + (endTime - startTime) + "ms");

		// 跳转到 message.jsp
		getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
	}

	public static void getImage(String inputPath) {
		try {
			FileInputStream fis = new FileInputStream(inputPath);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.skip(0x12);
			byte[] b1 = new byte[4];
			bis.read(b1);
			width = byte2Int(b1);
			byte[] b2 = new byte[4];
			bis.read(b2);
			height = byte2Int(b2);
			System.out.println(width + "*" + height);
			int start = 0x36;
			bis.skip(start - 0x12 - 4 - 4);
			Images = new int[height][width * 3];
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width * 3; j++) {
					Images[i][j] = bis.read();
				}
			}
			bis.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int byte2Int(byte[] by) {
		int t1 = by[3] & 0xff;
		int t2 = by[2] & 0xff;
		int t3 = by[1] & 0xff;
		int t4 = by[0] & 0xff;
		int num = t1 << 24 | t2 << 16 | t3 << 8 | t4;
		return num;
	}
}
