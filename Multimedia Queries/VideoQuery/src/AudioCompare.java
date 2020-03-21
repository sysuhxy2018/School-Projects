
import com.musicg.wave.Wave;
import com.musicg.fingerprint.FingerprintManager;
import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.fingerprint.FingerprintSimilarityComputer;
import java.util.ArrayList;

public class AudioCompare{

	private double audioMatchRate;

	public AudioCompare(){
		this.audioMatchRate = audioMatchRate;
	}

	public ArrayList<DataClip> getAudioMatchRate(String wavPath, String rgbPath) {

		ArrayList<DataClip> result = new ArrayList<>();
		String[] filename = {
				"database_videos/flowers/flowers",
				"database_videos/interview/interview",
				"database_videos/movie/movie",
				"database_videos/musicvideo/musicvideo",
				"database_videos/sports/sports",
				"database_videos/starcraft/StarCraft",
				"database_videos/traffic/traffic"
		};
		// create wave objects list
		ArrayList<Wave> dbWave = new ArrayList<>();
		dbWave.add(0, new Wave(filename[0]+".wav"));
		dbWave.add(1, new Wave(filename[1]+".wav"));
		dbWave.add(2, new Wave(filename[2]+".wav"));
		dbWave.add(3, new Wave(filename[3]+".wav"));
		dbWave.add(4, new Wave(filename[4]+".wav"));
		dbWave.add(5, new Wave(filename[5]+".wav"));
		dbWave.add(6, new Wave(filename[6]+".wav"));

		//create query wave object
		Wave query = new Wave(wavPath+".wav");
		byte[] secondFingerPrint = new FingerprintManager().extractFingerprint(query);

		for(int i = 0; i < 7; i++){
			// Fingerprint from WAV
			byte[] firstFingerPrint = new FingerprintManager().extractFingerprint(dbWave.get(i));
			// Compare fingerprints
			FingerprintSimilarity fingerprintSimilarity = new FingerprintSimilarityComputer(firstFingerPrint, secondFingerPrint).getFingerprintsSimilarity();
			float score = fingerprintSimilarity.getScore();
			int position = fingerprintSimilarity.getMostSimilarFramePosition();
			//System.out.println(i + ": Similarity score = " + score);
			//System.out.println(i + ": Similarity position = " + position);

			//generate audio match rate
			if(score >= 1) {
				audioMatchRate = 99;
			}
			else{
				audioMatchRate = score * 100;
			}
			//generate matched position
			int lowerBound = position;
			result.add(new DataClip(filename[i],filename[i], lowerBound, lowerBound+149, audioMatchRate) );

		}
		return result;

	}

}