import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class AudioCaptcha
{

	public static void main(String[] args)
	{
        try {
            if ((args != null) && (args.length == 2)) {
                AudioCaptcha.captchaToFile(args[0], args[1]);
            } else {
                System.out.println("Usage: java -jar AudioCaptcha.jar captchaText filePath");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    public static void captchaToFile(String captcha, String path) throws IOException
	{
        FileOutputStream out = new FileOutputStream(new File(path));
        AudioCaptcha.captcha(captcha, out);
        out.flush();
        out.close();
	}
    
    public static void captcha(String captcha, OutputStream out) throws IOException {
        ArrayList<String> files = new ArrayList<String>();
        for (int i = 0; i < captcha.length(); i++) {
            String c = captcha.substring(i, i + 1);
            files.add("/chars/" + c + ".wav");
        }
        Merger m = new Merger();
        m.mergeListOfFiles(files, out);
        out.flush();
        out.close();
    }

}
