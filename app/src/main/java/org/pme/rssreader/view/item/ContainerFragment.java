package org.pme.rssreader.view.item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.pme.rssreader.R;
import org.pme.rssreader.view.item.details.ItemDetailsFragment;
import org.pme.rssreader.view.item.list.ItemListFragment;

/**
 * Fragment that orchestrates ItemListFragment and ItemDetailsFragment depending on device orientation.
 */
public class ContainerFragment extends Fragment {

    private Fragment itemListFragment;
    public static String ITEM_LIST_FRAGMENT_TAG = "ITEM_LIST";

    private Fragment itemDetailsFragment;
    public static String ITEM_DETAILS_FRAGMENT_TAG = "ITEM_DETAILS";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_fragment_layout, container, false);

        if (getArguments() == null) {
            return view;
        }

        inflateItemListContainer();

        if (view.findViewById(R.id.item_detail_container) != null) {
            // Dual pane mode
            inflateItemDetailContainer();
        }

        return view;
    }

    private void inflateItemListContainer() {
        FragmentManager fm = requireActivity().getSupportFragmentManager();

        Fragment fragment = fm.findFragmentByTag(ITEM_LIST_FRAGMENT_TAG);

        if (fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }

        // Create
        itemListFragment = ItemListFragment.newInstance();
        Bundle bundle = requireArguments(); // Pass own bundle for "currentFeedId" and FEED_TITLE
        itemListFragment.setArguments(bundle);

        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.item_list_container, itemListFragment, ITEM_LIST_FRAGMENT_TAG);
        ft.commit();
    }

    private void inflateItemDetailContainer() {
        FragmentManager fm = requireActivity().getSupportFragmentManager();

        Fragment fragment = fm.findFragmentByTag(ITEM_DETAILS_FRAGMENT_TAG);

        if (fragment != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }

        // Create
        itemDetailsFragment = ItemDetailsFragment.newInstance();

        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.item_detail_container, itemDetailsFragment, ITEM_DETAILS_FRAGMENT_TAG);
        ft.commit();
    }

}
