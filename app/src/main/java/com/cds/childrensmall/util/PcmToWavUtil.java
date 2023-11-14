package com.cds.childrensmall.util;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class PcmToWavUtil {
    private int mBufferSize; //缓存的音频大小
    private int mSampleRate = 16000;// 此处的值必须与录音时的采样率一致
    private int mChannel = AudioFormat.CHANNEL_IN_MONO; //立体声
    private int mEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private static class SingleHolder {
        static PcmToWavUtil mInstance = new PcmToWavUtil();
    }

    public static PcmToWavUtil getInstance() {
        return SingleHolder.mInstance;
    }


    public PcmToWavUtil() {

        Log.i("AudioRecordUtil", "PcmToWavUtil:mChannel "+mChannel +"mEncoding:"+mEncoding+"mSampleRate:"+mSampleRate);
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, mEncoding);
    }

    /**
     * @param sampleRate sample rate、采样率
     * @param channel    channel、声道
     * @param encoding   Audio data format、音频格式
     */
    public PcmToWavUtil(int sampleRate, int channel, int encoding) {
        this.mSampleRate = sampleRate;
        this.mChannel = channel;
        this.mEncoding = encoding;
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, mEncoding);
    }




    public interface  OnSuccessFunLisener{

        public void onSuccessFun();

        public void onOnFail();


    }

    /**
     * pcm文件转wav文件
     *
     * @param inFilename  源文件路径
     * @param outFilename 目标文件路径
     * @param deleteOrg   是否删除源文件
     */
    public void pcmToWav(String inFilename, String outFilename, boolean deleteOrg, OnSuccessFunLisener onSuccessFunLisener) throws IOException {
        FileInputStream in;
        FileOutputStream out;
        long totalAudioLen;
        long totalDataLen;
        long longSampleRate = mSampleRate;
        int channels = mChannel == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
        //long byteRate = 16 * mSampleRate * channels / 8;
        long byteRate = mEncoding * longSampleRate * channels /8;
        byte[] data = new byte[mBufferSize];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            writeWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            Log.i("11","--->pcm转换成！");
            in.close();
            out.flush();
            out.close();
            onSuccessFunLisener.onSuccessFun();
            if (deleteOrg) {
                new File(inFilename).delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pcmToWav(String inFilename, String outFilename, OnSuccessFunLisener onSuccessFunLisener) throws IOException {
        pcmToWav(inFilename, outFilename, false,onSuccessFunLisener);
    }

    /**
     * 加入wav文件头
     */
    private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        // ChunkID, RIFF, 占4bytes
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        // ChunkSize, pcmLen + 36, 占4bytes
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        // Format, WAVE, 占4bytes
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        // Subchunk1ID, 'fmt ', 占4bytes
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        // Subchunk1Size, 16, 占4bytes
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // AudioFormat, pcm = 1, 占2bytes
        header[20] = 1;
        header[21] = 0;
        // NumChannels, mono = 1, stereo = 2, 占2bytes
        header[22] = (byte) channels;
        header[23] = 0;
        // SampleRate, 占4bytes
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        // ByteRate = SampleRate * NumChannels * BitsPerSample / 8, 占4bytes
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // BlockAlign = NumChannels * BitsPerSample / 8, 占2bytes
        header[32] = (byte) (channels * mEncoding / 8);
        header[33] = 0;
        // BitsPerSample, 占2bytes
        header[34] = 16;
        header[35] = 0;
        // Subhunk2ID, data, 占4bytes
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        // Subchunk2Size, 占4bytes
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
