package Class;

import java.util.ArrayList;

import fm.giik.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PodcastAdapter extends ArrayAdapter<Podcast> {
	ArrayList<Podcast> podcast;
	Context CTX;
	int PlayID;

	public PodcastAdapter(Context context, ArrayList<Podcast> objects, int PlayID) {
		super(context, R.layout.list_row, objects);
		this.podcast = objects;
		CTX = context;
		this.PlayID=PlayID;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) CTX
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.list_row, null);
		}

		TextView Baslik = (TextView) convertView.findViewById(R.id.Baslik);
		TextView YuklenmeTarih = (TextView) convertView
				.findViewById(R.id.UploadDate);

		TextView URL = new TextView(CTX);

		TextView AudioURL = new TextView(CTX);

		ImageView Image = (ImageView) convertView.findViewById(R.id.list_image);
	
		
		Image.setImageDrawable(null);
		AudioURL.setText(podcast.get(position).getAudioURL());
		URL.setText(podcast.get(position).getURL());
		Baslik.setText(podcast.get(position).getBaslik());
		YuklenmeTarih.setText(podcast.get(position).getYuklenmeTarih());

		return convertView;
	}
}