package org.pme.rssreader.view.feed.newfeed;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

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
public class NewFeedFragment extends DialogFragment {

    public static final String FRAGMENT_TAG = "DIALOG_FRAGMENT";

    private EditText nameText;
    private EditText linkText;

    private View view;

    FeedRepository feedRepository;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.fragment_new_feed, null);

        // Text
        nameText = view.findViewById(R.id.text_input_new_feed_name);
        linkText = view.findViewById(R.id.text_input_new_feed_link);

        // Repo
        feedRepository = FeedRepository.getRepository(requireContext());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because it's going into the dialog layout
        builder.setView(view)
            .setTitle(R.string.add_a_new_feed)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();

        // Positive button listener
        dialog.setOnShowListener(dialogInterface -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> saveNewFeed());
        });

        return dialog;
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

        // Try HTTPS for HTTP URLs, since we don't support insecure requests
        url = url.replace("http://", "https://");

        // Add https if needed
        if (!url.startsWith("https://")) {
            url = String.format("https://%s", url);
        }

        String finalUrl = url;

        // Validate XML
        validateXML(finalUrl, new ValidationCallback() {
            @Override
            public void onSuccess() {
                Feed f = new Feed(nameText.getText().toString(), finalUrl);
                feedRepository.insert(f);
                showSnackbar(getString(R.string.feed_saved));
                // Wait for snackbar feedback
                (new Handler()).postDelayed(NewFeedFragment.this::dismiss, 1500);
            }

            @Override
            public void onFailure() {
                showSnackbar(getString(R.string.feed_not_available));
            }
        });
    }

    private void showSnackbar(String text) {
        Snackbar snackbar = Snackbar.make(view,
                text,
                Snackbar.LENGTH_SHORT);
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
