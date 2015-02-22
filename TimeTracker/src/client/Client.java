package client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Client {
	
	private static final String URL = "flakenerd.no-ip.info";
	private int port;
	
	private char[] buffer;
	
	public Client(int port) {
		this.port = port;
		buffer = new char[1024];
	}
	
	public void connect() {
		try {
			Socket socket = new Socket();
			InetSocketAddress address = new InetSocketAddress(URL, port);
			socket.connect(address);
			OutputStreamWriter toServerWriter = new OutputStreamWriter(socket.getOutputStream());
			InputStreamReader fromServerReader = new InputStreamReader(socket.getInputStream());
			toServerWriter.write("time");
			toServerWriter.flush();
			run(toServerWriter, fromServerReader);
			toServerWriter.close();
			fromServerReader.close();
			socket.close();
		} catch (IOException e) {
			
		}
	}
	
	private void run(OutputStreamWriter writer, InputStreamReader reader) {
		try {
			boolean openConnection = true;
			Scanner scanner = new Scanner(System.in);
			String input = "";
			String text;
			while(openConnection) {
				int l = reader.read(buffer);
				text = String.valueOf(buffer).substring(0, l);
				System.out.println(text);
				if(text.equals("Please send your username")) {
					input = scanner.nextLine();
					input = encode(input);
					writer.write(input);
					writer.flush();
				}
				else if(text.equals("Please send your password")) {
					input = scanner.nextLine();
					input = encode(input);
					writer.write(input);
					writer.flush();
				}
				else {
					input = scanner.nextLine();
					if(input.equals("0") || input.equals("1") || input.equals("2") || input.equals("3")) {
						writer.write(input);
						writer.flush();
						l = reader.read(buffer);
						text = String.valueOf(buffer).substring(0, l);
						System.out.println(text);
						openConnection = false;
					}
					else {
						System.out.println("Please write 0, 1, 2 or 3");
					}
				}
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Encodes a given string with SHA-512.
	 * @param toEncode
	 * @return
	 */
	private String encode(String toEncode) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-512");
			byte[] res = new byte[1024];
			digest.update(toEncode.getBytes("UTF-8"), 0, toEncode.length());
			res = digest.digest();
			return convert(res);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			return null;
		}
	}
	
	private String convert(byte[] data) {
		StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++)
        {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do
            {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            }
            while(two_halfs++ < 1);
        }
        return buf.toString();
	}
	
}
