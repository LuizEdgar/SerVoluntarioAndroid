package com.lutzed.servoluntario.completion;

import android.graphics.Bitmap;

import com.lutzed.servoluntario.interfaces.BasePresenter;
import com.lutzed.servoluntario.interfaces.BaseView;

/**
 * Created by luizfreitas on 18/04/2017.
 */

public interface VolunteerCompletionContract {

    interface View extends BaseView<Presenter> {
        void setSavingIndicator(boolean active);

        void navigateToChooseSkills();

        void resetErrors();

        void navigateToMain();

        void navigateToChooseCauses();

        void setAboutField(String about);

        void setOccupationField(String occupation);

        void showDefaultSaveError();

        void showProfileImageTypePicker();

        void showAddNewImageFromGallery(int request);

        void showAddNewImageFromCamera(int request);

        void onRequestPermissionsResultFromPresenter(int requestCode, String[] permissions, int[] grantResults);

        void setProfileImage(Bitmap bitmap);

        void setProfileImage(String url);

        void setLoadingIndicator(boolean active);
    }

    interface Presenter extends BasePresenter {
        void saveProfile(String about, String occupation);

        void addNewProfileImage();

        void addNewImageFromCamera(int request);

        void addNewImageFromGallery(int request);

        void onNewProfileImageAdded(Bitmap proportionalResizedBitmap);
    }
}
