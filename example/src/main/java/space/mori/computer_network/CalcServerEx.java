package space.mori.computer_network;

import java.io.*;
import java.net.*;
import java.util.*;

public class CalcServerEx {
	public static String calc(String exp) {
		StringTokenizer st = new StringTokenizer(exp, " ");
		if (st.countTokens() != 3) return "error";

		String res="";
		int op1 = Integer.parseInt(st.nextToken());
		String opcode = st.nextToken();
		int op2 = Integer.parseInt(st.nextToken());
		switch (opcode) {
			case "+": res = Integer.toString(op1 + op2);
				break;
			case "-": res = Integer.toString(op1 - op2);
				break;
			case "*": res = Integer.toString(op1 * op2);
				break;
			default : res = "error";
		}
		return res;
	}

	public static void main(String[] args) {
		BufferedReader in = null;
		BufferedWriter out = null;
		ServerSocket listener = null;
		Socket socket = null;
		try {
			listener = new ServerSocket(9999); // ���� ���� ����
			System.out.println("������ ��ٸ��� �ֽ��ϴ�.....");			
			socket = listener.accept(); // Ŭ���̾�Ʈ�κ��� ���� ��û ���
			System.out.println("����Ǿ����ϴ�.");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // ���� �Է½�Ʈ��
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // ���� ��� ��Ʈ��
			while (true) {
				String inputMessage = in.readLine(); // Ŭ���̾�Ʈ�κ��� �� �� �б�. ����
				if (inputMessage.equalsIgnoreCase("bye")) {
					System.out.println("Ŭ���̾�Ʈ���� ������ �����Ͽ���"); // ���� �޽����� ȭ�鿡 ���
					break; // "bye"�� ������ ���� ����
				}
				System.out.println(inputMessage); // ���� �޽����� ȭ�鿡 ���
				String res = calc(inputMessage);
				out.write(res + "\n"); // Ű���忡�� ���� ���ڿ� ����
				out.flush();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if(socket != null) socket.close(); // ��ſ� ���� �ݱ�
				if(listener != null) listener.close(); // ���� ���� �ݱ�
			} catch (IOException e) {
				System.out.println("Ŭ���̾�Ʈ�� ä�� �� ������ �߻��߽��ϴ�.");
			}
		}
	}
}