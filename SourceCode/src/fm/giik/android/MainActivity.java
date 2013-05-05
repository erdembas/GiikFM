package fm.giik.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import Class.*;
import org.xml.sax.SAXException;

import fm.giik.android.R;

import Class.Podcast;
import Class.PodcastAdapter;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.RSSurfaceView;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnClickListener,
		OnTouchListener, OnBufferingUpdateListener, OnCompletionListener {
	ListView lvPodcast;
	ArrayList<Podcast> PodcastListe = new ArrayList<Podcast>();

	private MediaPlayer mediaPlayer;
	private int mediaFileLengthInMilliseconds;
	String SimdiCalan = null;
	ImageButton Stop;
	SeekBar seekBarProgress;
	TextView txtSimdiCalan;
	TextView txtDuration;

	LinearLayout StopButtons;
	RelativeLayout Player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lvPodcast = (ListView) findViewById(R.id.PodcastList);
		
		if (NetState.Kontrol(this)) {
			new GetPodCastList().execute();
		}else {
			Dialog.showAlertDialog(this, "Ýnternet Baðlantýsý", "Uygulamayý kullanabilmeniz için internet baðlantýsý gereklidir!", false);
		}
		
		Player =(RelativeLayout)findViewById(R.id.Player);
		
		seekBarProgress = (SeekBar) findViewById(R.id.seekBar1);
		seekBarProgress.setMax(99);
		seekBarProgress.setOnTouchListener(this);

		txtSimdiCalan = (TextView) findViewById(R.id.txtSimdiCalan);
		txtSimdiCalan.setSelected(true);

		txtDuration = (TextView) findViewById(R.id.txtDuration);

		Stop = (ImageButton) findViewById(R.id.Stop);
		Stop.setOnClickListener(this);

		StopButtons = (LinearLayout) findViewById(R.id.Buttons2);
		StopButtons.setVisibility(View.GONE);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);
		
		Player.setVisibility(View.GONE);

		lvPodcast.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {

				AlertDialog.Builder listIslem = new AlertDialog.Builder(
						MainActivity.this);
				listIslem.setTitle("Ýþlem Seçiniz");
				final CharSequence[] items = { "Ýndir", "Dinle", "Vazgeç" };
				listIslem.setItems(items,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								dialog.cancel();
								if (item == 0) {
									String DownloadUrl = PodcastListe.get(arg2).getAudioURL().toString();
									 String fileName = DownloadUrl.substring(DownloadUrl.lastIndexOf('/') + 1);
									    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadUrl));
									    request.setDescription(PodcastListe.get(arg2).getBaslik());  
									    request.setTitle("Dosya Boyutu: " + String.valueOf(FileSizeFormatter.formatFileSize(PodcastListe.get(arg2).getDosyaBoyut())));                 
									    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
									        request.allowScanningByMediaScanner();
									        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
									    }
									    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,  fileName);
									    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
									    manager.enqueue(request);
								} else if (item == 1) {
									SimdiCalan = PodcastListe.get(arg2)
											.getAudioURL();
									txtSimdiCalan.setText(PodcastListe
											.get(arg2).getBaslik());

									new PodcastPlay().execute();
								} else {
									dialog.cancel();
								}
							}
						});
				listIslem.show();
			}
		});
	}

	private void SeekBarProgressUpdater() {
		seekBarProgress.setProgress((int) (((float) mediaPlayer
				.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));
		if (mediaPlayer.isPlaying()) {
			Runnable notification = new Runnable() {
				public void run() {
					SeekBarProgressUpdater();
				}
			};
		}
	}

	public class PodcastPlay extends AsyncTask<Void, Void, Void> {

		private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

		@Override
		protected void onPostExecute(Void result) {
			dialog.dismiss();
			Player.setVisibility(View.VISIBLE);
			txtDuration.setVisibility(View.VISIBLE);
			txtSimdiCalan.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Oynatmaya Hazýrlanýyor...");
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			mediaPlayer.reset();
			mediaPlayer.stop();
			seekBarProgress.setProgress(0);
			seekBarProgress.setSecondaryProgress(0);
			try {
				mediaPlayer.setDataSource(SimdiCalan);
				mediaPlayer.prepare();
			} catch (Exception e) {
				// TODO: handle exception
			}
			mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
			mediaPlayer.start();
			return null;
		}

	}

	public class GetPodCastList extends AsyncTask<Void, Void, Void> {

		private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

		@Override
		protected void onPostExecute(Void result) {
			lvPodcast.setAdapter(new PodcastAdapter(getApplicationContext(),
					PodcastListe, -1));
			dialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog.setMessage("Yükleniyor...");
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			PodcastListe = GetListfromRss();
			return null;
		}

	}

	public String MilisecondToTime(int Milisecond) {
		return String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(Milisecond),
				TimeUnit.MILLISECONDS.toMinutes(Milisecond)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(Milisecond)), // The change is in this
														// line
				TimeUnit.MILLISECONDS.toSeconds(Milisecond)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(Milisecond)));
	}

	public ArrayList<Podcast> GetListfromRss() {
		ArrayList<Podcast> List = new ArrayList<Podcast>();
		try {
			URL url = new URL("http://feeds.feedburner.com/giikfm?format=xml");
			DocumentBuilderFactory dFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dFactory.newDocumentBuilder();

			Document document = dBuilder
					.parse(new InputSource(url.openStream()));
			document.getDocumentElement().normalize();
			NodeList nodeListCountry = document
					.getElementsByTagName("media:content");
			for (int i = 0; i < nodeListCountry.getLength(); i++) {
				Element mediacontentElement = (Element) nodeListCountry.item(i);
				try {
					Node node = mediacontentElement.getParentNode();
					Element elementMain = (Element) node;

					NodeList BaslikText = elementMain
							.getElementsByTagName("title");
					Element Baslik = (Element) BaslikText.item(0);

					NodeList YuklenmeText = elementMain
							.getElementsByTagName("pubDate");
					Element Yuklenme = (Element) YuklenmeText.item(0);

					NodeList URLText = elementMain.getElementsByTagName("link");
					Element URL = (Element) URLText.item(0);

					Date Tarih = new Date(Yuklenme.getChildNodes().item(0)
							.getNodeValue());
					Podcast Yeni = new Podcast();
					Yeni.setBaslik(Baslik.getChildNodes().item(0)
							.getNodeValue());
					Yeni.setYuklenmeTarih(Tarih.toLocaleString());
					Yeni.setURL(URL.getChildNodes().item(0).getNodeValue());
					Yeni.setAudioURL(mediacontentElement.getAttribute("url"));
					Yeni.setDosyaBoyut(Double.parseDouble(mediacontentElement
							.getAttribute("fileSize")) / 1024);

					List.add(Yeni);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return List;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Stop:
			Player.setVisibility(View.GONE);
			mediaPlayer.reset();
			mediaPlayer.stop();
			seekBarProgress.setProgress(0);
			seekBarProgress.setSecondaryProgress(0);
			txtSimdiCalan.setVisibility(View.GONE);
			txtDuration.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.seekBar1) {
			if (mediaPlayer.isPlaying()) {
				SeekBar sb = (SeekBar) v;
				int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100)
						* sb.getProgress();
				mediaPlayer.seekTo(playPositionInMillisecconds);
			}
		}
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		txtSimdiCalan.setVisibility(View.GONE);
		txtDuration.setVisibility(View.GONE);
		StopButtons.setVisibility(View.GONE);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		String CurrentPosition = MilisecondToTime(mediaPlayer
				.getCurrentPosition());
		String ToplamSure = MilisecondToTime(mediaFileLengthInMilliseconds);

		txtDuration.setText(CurrentPosition + " / " + ToplamSure);
		seekBarProgress.setSecondaryProgress(percent);
		StopButtons.setVisibility(View.VISIBLE);
	}

}
