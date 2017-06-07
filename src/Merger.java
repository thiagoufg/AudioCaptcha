import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Created by Jon Mercer on 15-09-12.
 */
public class Merger
{

	public void mergeListOfFiles(ArrayList<InputStream> filesToMerge, OutputStream out)
	{

		// TODO: what if size is 0?
		if (filesToMerge.size() == 1)
		{
			try
			{
				AudioInputStream onlyWav = AudioSystem.getAudioInputStream(filesToMerge.get(0));
				writeWav(onlyWav, out);
			} catch (UnsupportedAudioFileException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		} else if (filesToMerge.size() > 1)
		{
			try
			{
				AudioInputStream growingWav;
				growingWav = AudioSystem.getAudioInputStream(filesToMerge.get(0));

				for (int i = 1; i < filesToMerge.size(); i++)
				{
					AudioInputStream toAppend = AudioSystem.getAudioInputStream(filesToMerge.get(i));
					growingWav = new AudioInputStream(new SequenceInputStream(growingWav, toAppend), growingWav.getFormat(),
							growingWav.getFrameLength() + toAppend.getFrameLength());
				}

				writeWav(growingWav, out);

			} catch (UnsupportedAudioFileException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void writeWav(AudioInputStream wav, OutputStream out)
	{

		try
		{
			AudioSystem.write(wav, AudioFileFormat.Type.WAVE, out);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public byte[] getBytes(InputStream is)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			AudioInputStream ais;
			ais = AudioSystem.getAudioInputStream(is);
			int read;
			byte[] buffer = new byte[1024];
			while ((read = ais.read(buffer)) != -1)
			{
				baos.write(buffer, 0, read);
			}
			baos.flush();
			return baos.toByteArray();
		} catch (Exception e)
		{
			return null;
		}
	}
	
	private byte[] insertNoise(byte[] bufferA)
	{
		return null;
	}

	private byte[] mixBuffers(byte[] bufferA, byte[] bufferB)
	{
		byte[] array = new byte[bufferA.length];

		for (int i = 0; i < bufferA.length; i += 2)
		{
			short buf1A = bufferA[i + 1];
			short buf2A = bufferA[i];
			buf1A = (short) ((buf1A & 0xff) << 8);
			buf2A = (short) (buf2A & 0xff);

			short buf1B = bufferB[i + 1];
			short buf2B = bufferB[i];
			buf1B = (short) ((buf1B & 0xff) << 8);
			buf2B = (short) (buf2B & 0xff);

			short buf1C = (short) (buf1A + buf1B);
			short buf2C = (short) (buf2A + buf2B);

			short res = (short) (buf1C | buf2C);

			array[i] = (byte) res;
			array[i + 1] = (byte) (res >> 8);
		}

		return array;
	}

}