package org.pme.rssreader.view.feed.newfeed;

import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.pme.rssreader.MainActivity;
import org.pme.rssreader.R;
import org.pme.rssreader.network.NetworkApi;
import org.pme.rssreader.network.NetworkController;
import org.pme.rssreader.network.model.XmlFeed;
import org.pme.rssreader.storage.FeedRepository;
import org.pme.rssreader.storage.model.Feed;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * View to add a new feed
 */
public class NewFeedFragment extends Fragment {

    private static final String LOG_TAG = "NewFeedActivity";
    private EditText nameText;
    private EditText linkText;

    private View view;

    FeedRepository feedRepository;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set help dialog text
        ((MainActivity) requireActivity()).setHelpDialogContent(getString(R.string.help_dialog_content_new_feed));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_feed, container, false);

        // Link Button with Click Listener
        Button saveFeed = view.findViewById(R.id.btn_save_feed);
        saveFeed.setOnClickListener(this.buttonClickListener);

        // Text
        nameText = view.findViewById(R.id.text_input_new_feed_name);
        linkText = view.findViewById(R.id.text_input_new_feed_link);

        // Repo
        feedRepository = FeedRepository.getRepository(requireContext());

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) requireActivity()).resetHelpDialogContent();
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Check which button was clicked
            if( v.getId() == R.id.btn_save_feed)
            {
                saveNewFeed();
            }
        }
    };

    private void goBack() {
        requireActivity().onBackPressed();
    }

    private void saveNewFeed() {
        // No empty fields
        if (nameText.getText().toString().equals("") || linkText.getText().toString().equals("")) {
            showSnackbar(getString(R.string.fill_in_fields));
            return;
        }

        // Validate url
        String url = linkText.getText().toString();
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            showSnackbar(getString(R.string.invalid_url));
            return;
        }

        // Validate XML
        validateXML(url, new ValidationCallback() {
            @Override
            public void onSuccess() {
                Feed f = new Feed(nameText.getText().toString(), linkText.getText().toString());
                feedRepository.insert(f);
                showSnackbar(getString(R.string.feed_saved));
                // Wait for snackbar feedback
                (new Handler()).postDelayed(NewFeedFragment.this::goBack, 1500);
            }

            @Override
            public void onFailure() {
                showSnackbar(getString(R.string.feed_not_available));
            }
        });
    }

    private void showSnackbar(String text) {
        Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content),
                text,
                Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private interface ValidationCallback {
        void onSuccess();
        void onFailure();
    }

    /**
     * Validates XML by trying to parse it
     */
    private void validateXML(String url, ValidationCallback validationCallback) {
        try {
            NetworkApi api = NetworkController.getApi();
            api.getFeed(url).enqueue(new Callback<XmlFeed>() {
                @Override
                public void onResponse(@NonNull Call<XmlFeed> call, @NonNull Response<XmlFeed> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().channel.item.size() > 0) {
                                validationCallback.onSuccess();
                                return;
                            }
                        }
                    }
                    validationCallback.onFailure();
                }

                @Override
                public void onFailure(@NonNull Call<XmlFeed> call, @NonNull Throwable t) {
                    validationCallback.onFailure();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
