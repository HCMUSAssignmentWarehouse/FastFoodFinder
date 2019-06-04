package com.iceteaviet.fastfoodfinder.ui.profile.cover

import android.graphics.drawable.Drawable
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider

/**
 * Created by tom on 2019-04-18.
 */
class UpdateCoverPresenter : BasePresenter<UpdateCoverContract.Presenter>, UpdateCoverContract.Presenter {

    private val updateCoverView: UpdateCoverContract.View

    private var selectedImage: Drawable? = null

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, updateCoverView: UpdateCoverContract.View) : super(dataManager, schedulerProvider) {
        this.updateCoverView = updateCoverView
        this.updateCoverView.presenter = this
    }

    override fun subscribe() {
    }

    override fun onImageBrowserButtonClick() {
        updateCoverView.openImageFilePicker()
    }

    override fun onCoverImageSelect(selectedImage: Drawable) {
        this.selectedImage = selectedImage

        updateCoverView.setSelectedImage(selectedImage)
    }

    override fun onDoneButtonClick() {
        updateCoverView.dismissWithResult(selectedImage)
    }
}