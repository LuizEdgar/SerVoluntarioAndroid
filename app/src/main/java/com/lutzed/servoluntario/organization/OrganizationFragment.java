package com.lutzed.servoluntario.organization;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lutzed.servoluntario.R;
import com.lutzed.servoluntario.adapters.GalleryViewAdapter;
import com.lutzed.servoluntario.adapters.OpportunitiesItemsAdapter;
import com.lutzed.servoluntario.login.LoginActivity;
import com.lutzed.servoluntario.models.Contact;
import com.lutzed.servoluntario.models.Image;
import com.lutzed.servoluntario.models.SelectableItem;
import com.lutzed.servoluntario.user.EditUserActivity;
import com.lutzed.servoluntario.util.CircleTransform;
import com.lutzed.servoluntario.util.DataView;
import com.lutzed.servoluntario.util.Snippets;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class OrganizationFragment extends Fragment implements OrganizationContract.View {
    private static final String BUNDLE_CAN_EDIT = "bundle_can_edit";
    private static final int REQUEST_EDIT = 290;

    @BindView(R.id.profileImage) ImageView mProfileImage;
    @BindView(R.id.title) TextView mNameView;
    @BindView(R.id.about) TextView mAboutView;
    @BindView(R.id.location) DataView mLocationView;
    @BindView(R.id.contact) DataView mContactView;
    @BindView(R.id.site) DataView mSiteView;
    @BindView(R.id.imagesRecyclerView) RecyclerView mGalleryRecyclerView;
    @BindView(R.id.causesRecyclerView) RecyclerView mCausesRecyclerView;
    @BindView(R.id.skillsRecyclerView) RecyclerView mSkillsRecyclerView;
    @BindView(R.id.establishedAt) DataView mEstablishedAtView;
    @BindView(R.id.mission) DataView mMissionView;
    @BindView(R.id.size) DataView mSizeView;
    @BindView(R.id.cnpj) DataView mCnpjView;
    @BindView(R.id.othersWrapper) View mOthersWrapper;
    @BindView(R.id.causesWrapper) View mCausesWrapper;
    @BindView(R.id.skillsWrapper) View mSkillsWrapper;
    @BindView(R.id.galleryWrapper) View mGalleryWrapper;

    private OrganizationContract.Presenter mPresenter;

    private Listener mListener;
    private boolean mCanEdit;

    private ProgressDialog mLoadingProgress;

    public static OrganizationFragment newInstance(boolean canEdit) {
        OrganizationFragment fragment = new OrganizationFragment();
        Bundle args = new Bundle();
        args.putBoolean(BUNDLE_CAN_EDIT, canEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setPresenter(OrganizationContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCanEdit = getArguments().getBoolean(BUNDLE_CAN_EDIT);
        }

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_organization, container, false);
        ButterKnife.bind(this, root);

        LinearLayoutManager causesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mCausesRecyclerView.setLayoutManager(causesLayoutManager);

        LinearLayoutManager skillsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mSkillsRecyclerView.setLayoutManager(skillsLayoutManager);

        LinearLayoutManager imagesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mGalleryRecyclerView.setLayoutManager(imagesLayoutManager);

        mCausesRecyclerView.setAdapter(new OpportunitiesItemsAdapter(new ArrayList<SelectableItem>(), new OpportunitiesItemsAdapter.OnAdapterListener() {
            @Override
            public void onAdapterInteraction(SelectableItem mItem, int adapterPosition) {
            }
        }));
        mSkillsRecyclerView.setAdapter(new OpportunitiesItemsAdapter(new ArrayList<SelectableItem>(), new OpportunitiesItemsAdapter.OnAdapterListener() {
            @Override
            public void onAdapterInteraction(SelectableItem mItem, int adapterPosition) {
            }
        }));
        mGalleryRecyclerView.setAdapter(new GalleryViewAdapter(new ArrayList<Image>(), false, new GalleryViewAdapter.OnGalleryInteractionListener() {
            @Override
            public void onImageClicked(List<Image> values, Image item, int position) {
            }

            @Override
            public void onPromptDeleteImage(Image image, int position) {
            }
        }));

        if (mListener == null) {
            mProfileImage.setVisibility(View.VISIBLE);
        }

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_options, menu);

        if (mCanEdit) {
            menu.findItem(R.id.action_edit).setVisible(true);
            menu.findItem(R.id.action_sign_out).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            mPresenter.onEditOrganizationClicked();
            return true;
        } else if (item.getItemId() == R.id.action_sign_out) {
            mPresenter.signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void setLoadingIndicator(final boolean active) {
        if (active) {
            if (mLoadingProgress == null) {
                mLoadingProgress = Snippets.createProgressDialog(getContext(), R.string.loading_progress);
                mLoadingProgress.setCancelable(false);
            }
            if (!mLoadingProgress.isShowing()) {
                mLoadingProgress.show();
            }
        } else {
            if (mLoadingProgress != null && mLoadingProgress.isShowing()) {
                mLoadingProgress.dismiss();
                mLoadingProgress = null;
            }
        }
    }

    @Override
    public void setCauses(List<? extends SelectableItem> causes) {
        if (causes == null || causes.isEmpty()) {
            mCausesRecyclerView.setVisibility(View.GONE);
            mCausesWrapper.setVisibility(View.GONE);
        } else {
            OpportunitiesItemsAdapter adapter = (OpportunitiesItemsAdapter) mCausesRecyclerView.getAdapter();
            adapter.clearData();
            adapter.addItems(causes);
        }
    }

    @Override
    public void setSkills(List<? extends SelectableItem> skills) {
        if (skills == null || skills.isEmpty()) {
            mSkillsRecyclerView.setVisibility(View.GONE);
            mSkillsWrapper.setVisibility(View.GONE);
        } else {
            OpportunitiesItemsAdapter adapter = (OpportunitiesItemsAdapter) mSkillsRecyclerView.getAdapter();
            adapter.clearData();
            adapter.addItems(skills);
        }
    }

    @Override
    public void setImages(List<Image> images) {
        if (images != null && !images.isEmpty()) {
            mGalleryWrapper.setVisibility(View.VISIBLE);
            GalleryViewAdapter adapter = (GalleryViewAdapter) mGalleryRecyclerView.getAdapter();
            adapter.setItems(images);
        }
    }

    @Override
    public void setName(String title) {
        mNameView.setText(title);
    }

    @Override
    public void setAbout(String about) {
        if (TextUtils.isEmpty(about)) {
            mAboutView.setVisibility(View.GONE);
        } else {
            mAboutView.setText(about);
        }
    }

    @Override
    public void setCnpj(String cnpj) {
        if (cnpj == null) {
            mCnpjView.setVisibility(View.GONE);
        } else {
            mOthersWrapper.setVisibility(View.VISIBLE);
            mCnpjView.setData(cnpj);
        }

    }

    @Override
    public void setSite(String site) {
        if (TextUtils.isEmpty(site)) {
            mSiteView.setVisibility(View.GONE);
        } else {
            mSiteView.setData(site);
        }
    }

    @Override
    public void setMission(String mission) {
        if (TextUtils.isEmpty(mission)) {
            mMissionView.setVisibility(View.GONE);
        } else {
            mOthersWrapper.setVisibility(View.VISIBLE);
            mMissionView.setData(mission);
        }
    }

    @Override
    public void setLocation(String location) {
        if (TextUtils.isEmpty(location)) {
            mLocationView.setVisibility(View.GONE);
        } else {
            mLocationView.setData(location);
        }
    }

    @Override
    public void setEstablishedAt(String establishedAt) {
        if (TextUtils.isEmpty(establishedAt)) {
            mEstablishedAtView.setVisibility(View.GONE);
        } else {
            mOthersWrapper.setVisibility(View.VISIBLE);
            mEstablishedAtView.setData(establishedAt);
        }
    }

    @Override
    public void setContacts(List<Contact> contacts) {
        if (contacts != null && !contacts.isEmpty()) {
            mContactView.setVisibility(View.VISIBLE);
            StringBuilder builder = new StringBuilder();
            builder.append(contacts.get(0).toString());
            for (int i = 1; i < contacts.size(); i++) {
                builder.append("\n");
                builder.append(contacts.get(i).toString());
            }
            mContactView.setData(builder.toString());
        } else {
            mContactView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setCoverImage(String url) {
        if (mListener != null) mListener.onCoverImage(url);
        else
            Picasso.with(getContext()).load(url).transform(new CircleTransform(true)).placeholder(R.drawable.ic_user_placeholder).into(mProfileImage);
    }

    @Override
    public void showEditOrganization() {
        Intent intent = new Intent(getContext(), EditUserActivity.class);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_EDIT){
            mPresenter.loadOrganization();
        }
    }

    @Override
    public void signOut() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().finish();
        startActivity(intent);
    }

    @Override
    public void setSize(Integer size) {
        if (size == null) {
            mSizeView.setVisibility(View.GONE);
        } else {
            mOthersWrapper.setVisibility(View.VISIBLE);
            mSizeView.setData(String.valueOf(size));
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface Listener {
        void onCoverImage(String url);
    }

}

