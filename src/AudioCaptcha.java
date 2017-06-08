import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioCaptcha
{

	public static void main(String[] args)
	{
        AudioCaptcha a = new AudioCaptcha();
        try {
            if ((args != null) && (args.length == 2)) {
                a.captchaToFile(args[0], args[1]);
            } else {
                System.out.println("Usage: java -jar AudioCaptcha.jar captchaText filePath");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    public void captchaToFile(String captcha, String path) throws IOException
	{
        FileOutputStream out = new FileOutputStream(new File(path));
        captcha(captcha, out);
        out.flush();
        out.close();
	}
    
    public void captcha(String captcha, OutputStream out) throws IOException {
        ArrayList<String> files = new ArrayList<String>();
        for (int i = 0; i < captcha.length(); i++) {
            String c = captcha.substring(i, i + 1);
            files.add("/chars/" + c + ".wav");
        }
        mergeListOfFiles(files, out);
    }
    
    private void mergeListOfFiles(ArrayList<String> files, OutputStream out) {
        
        // TODO: what if size is 0?
        if (files.size() == 1) {
            try {
                AudioInputStream onlyWav = AudioSystem.getAudioInputStream(this.getClass().getResourceAsStream(
                        files.get(0)));
                writeWav(onlyWav, out);
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } else if (files.size() > 1) {
            try {
                AudioInputStream growingWav = AudioSystem.getAudioInputStream(this.getClass().getResourceAsStream(
                        files.get(0)));
                AudioInputStream growingWavInverse = AudioSystem.getAudioInputStream(this.getClass()
                        .getResourceAsStream(files.get(files.size() - 1)));
                
                for (int i = 1; i < files.size(); i++) {
                    AudioInputStream toAppend = AudioSystem.getAudioInputStream(this.getClass().getResourceAsStream(
                            files.get(i)));
                    growingWav = new AudioInputStream(new SequenceInputStream(growingWav, toAppend),
                            growingWav.getFormat(), growingWav.getFrameLength() + toAppend.getFrameLength());
                    
                    AudioInputStream toAppendInverse = AudioSystem.getAudioInputStream(this.getClass()
                            .getResourceAsStream(files.get(files.size() - 1 - i)));
                    growingWavInverse = new AudioInputStream(
                            new SequenceInputStream(growingWavInverse, toAppendInverse), growingWavInverse.getFormat(),
                            growingWavInverse.getFrameLength() + toAppendInverse.getFrameLength());
                }
                
                ByteArrayOutputStream captchaNormal = new ByteArrayOutputStream();
                writeWav(growingWav, captchaNormal);
                byte[] normal = captchaNormal.toByteArray();
                
                ByteArrayOutputStream captchaInverse = new ByteArrayOutputStream();
                writeWav(growingWavInverse, captchaInverse);
                
                byte[] inverse = captchaInverse.toByteArray();
                
                byte[] mixBuffers = mixBuffers(normal, inverse, true);
                
                AudioInputStream bis = AudioSystem.getAudioInputStream(new ByteArrayInputStream(mixBuffers));
                
                writeWav(bis, out);
                
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void writeWav(AudioInputStream wav, OutputStream out) {
        
        try {
            AudioSystem.write(wav, AudioFileFormat.Type.WAVE, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private byte[] getBytes(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AudioInputStream ais;
            ais = AudioSystem.getAudioInputStream(is);
            int read;
            byte[] buffer = new byte[1024];
            while ((read = ais.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
    
    private byte[] insertNoise(byte[] bufferA) {
        return null;
    }
    
    private byte[] mixBuffers(byte[] bufferA, byte[] bufferB, boolean limitedByA) {
        byte[] array = new byte[bufferA.length];
        
        for (int i = 0; i < 46; i++) {
            array[i] = bufferA[i];
        }
        
        for (int i = 46; i < (limitedByA ? bufferA.length : bufferB.length); i += 2) {
            short buf1A = bufferA[i + 1];
            short buf2A = Short.valueOf(Integer.valueOf(bufferA[i] / 12).toString());
            buf1A = (short) ((buf1A & 0xff) << 8);
            buf2A = (short) (buf2A & 0xff);
            
            short buf1B = bufferB[i + 1];
            short buf2B = Short.valueOf(Integer.valueOf(bufferB[i] / 12).toString());
            buf1B = (short) ((buf1B & 0xff) << 8);
            buf2B = (short) (buf2B & 0xff);
            
            short buf1C = (short) (buf1A + (buf1B / 12));
            short buf2C = (short) (buf2A + (buf2B / 12));
            
            short res = (short) (buf1C + buf2C);
            
            array[i] = (byte) res;
            array[i + 1] = (byte) (res >> 8);
        }
        
        return array;
    }

}
