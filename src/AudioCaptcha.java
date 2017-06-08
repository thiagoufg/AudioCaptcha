import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class AudioCaptcha
{

	public static void main(String[] args)
	{
        AudioCaptcha a = new AudioCaptcha("t0h4i7a9g1o");
	}

	public AudioCaptcha(String captcha)
	{
		try
		{
            ArrayList<String> files = new ArrayList<String>();
			for(int i=0;i<captcha.length();i++)
			{
				String c = captcha.substring(i,i+1);
                files.add("/chars/" + c + ".wav");
			}
			Merger m = new Merger();
            FileOutputStream out = new FileOutputStream(new File("c:\\users\\s202342\\desktop\\teste.wav"));
			m.mergeListOfFiles(files, out);
			out.flush();
			out.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
