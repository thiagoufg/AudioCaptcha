import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class AudioCaptcha
{

	public static void main(String[] args)
	{
		AudioCaptcha a = new AudioCaptcha("abcde");
	}

	public AudioCaptcha(String captcha)
	{
		try
		{
			ArrayList<InputStream> files = new ArrayList<InputStream>();
			for(int i=0;i<captcha.length();i++)
			{
				String c = captcha.substring(i,i+1);
				InputStream is = this.getClass().getResourceAsStream("/chars/"+c+".wav");
				files.add(is);
			}
			Merger m = new Merger();
			FileOutputStream out = new FileOutputStream(new File("d:\\teste.wav"));
			m.mergeListOfFiles(files, out);
			out.flush();
			out.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
