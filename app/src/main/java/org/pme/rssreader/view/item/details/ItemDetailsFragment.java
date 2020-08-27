package org.pme.rssreader.view.item.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.pme.rssreader.R;
import org.pme.rssreader.storage.model.Item;
import org.pme.rssreader.view.item.ItemViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * View for selected item details.
 * Also shown on dual-pane mode.
 */
public class ItemDetailsFragment extends Fragment {

    View view;
    ItemViewModel viewModel;

    public static ItemDetailsFragment newInstance() {
        return new ItemDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Hide help button from top bar
        menu.findItem(R.id.action_help).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Don't return a view if it won't be used
        if (container == null) { return null; }

        view = inflater.inflate(R.layout.fragment_detail, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        // Observe selected item and update fields accordingly
        viewModel.getItemSelectedEvent().observe(getViewLifecycleOwner(), currentItem -> {
            if (currentItem != null) {
                populateUIElements(currentItem);
            }
        });

        return view;
    }

    private void populateUIElements(Item currentItem) {

        TextView title = view.findViewById(R.id.item_view_detail_title);
        TextView content = view.findViewById(R.id.item_view_detail_content);
        TextView date = view.findViewById(R.id.item_view_detail_date);
        Button button = view.findViewById(R.id.item_view_detail_button);

        title.setText(currentItem.getTitle());

        // Publish date of the item
        Date pubDate = currentItem.getPubDate();

        // Show relative date (e.g. 3 hours ago) and full date together
        // unless it is so old, that the relative date is almost a full date.
        String dateText;
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.YYY HH:mm", Locale.getDefault());
        if (viewModel.getRelativeDateString(pubDate).contains("ago")) {
            dateText = String.format("%s (%s)", df.format(pubDate), viewModel.getRelativeDateString(pubDate));
        } else {
            dateText = df.format(pubDate);
        }
        date.setText(dateText);

        if (currentItem.getContent() != null) {
            content.setText(HtmlCompat.fromHtml(currentItem.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        } else {
            content.setText(HtmlCompat.fromHtml(currentItem.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        button.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(currentItem.getLink()));
            startActivity(i);
        });
    }

}