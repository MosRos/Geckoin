package com.mrostami.geckoin.presentation.widgets

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.mrostami.geckoin.R

class MenuItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs) {

    init {
        getAttributes(context, attrs, defStyle)
    }

    var rootLayout: View? = null
    private var txtTitle: TextView? = null
    private var txtSubTitle: TextView? = null
    private var txtDescription: TextView? = null
    private var imgStartIcon: ImageView? = null
    private var imgEndIcon: ImageView? = null
    private var dividerBottom: View? = null

    @FontRes
    var fontResId: Int? = null //R.font.firasans_regular
    set(value) {
        field = value
        updateTextTypeface()
    }

    var menuIcon: Drawable? = null
        set(value) {
            field = value
            imgStartIcon?.setImageDrawable(value)
        }
    var showMenuIcon: Boolean = true
        set(value) {
            field = value
            imgStartIcon?.isVisible = value
        }

    var title: String? = null
        set(value) {
            field = value
            txtTitle?.text = value
        }

    var subTitle: String? = null
        set(value) {
            field = value
            txtSubTitle?.text = value
        }

    var showSubTitle: Boolean = false
        set(value) {
            field = value
            txtSubTitle?.isVisible = value
        }

    var description: String? = null
        set(value) {
            field = value
            txtDescription?.text = value
        }

    var showDescription: Boolean = false
        set(value) {
            field = value
            txtDescription?.isVisible = value
        }

    var endIcon: Drawable? = null
        set(value) {
            field = value
            imgEndIcon?.setImageDrawable(value)
        }

    var showEndIcon: Boolean = true
        set(value) {
            field = value
            imgEndIcon?.isVisible = value
        }
    var showBottomDivider: Boolean = true
        set(value) {
            field = value
            dividerBottom?.isVisible = value
        }

    @StyleRes
    var titleTextAppearance: Int? = null
        @RequiresApi(Build.VERSION_CODES.M)
        set(value) {
            if (value != null) {
                txtTitle?.setTextAppearance(value)
            }
        }

    private fun getAttributes(ctx: Context, attrSet: AttributeSet? = null, def: Int = 0) {
        val attr = ctx.obtainStyledAttributes(attrSet, R.styleable.MenuItemView)

        val fontFamilyId: Int = attr.getResourceId(R.styleable.MenuItemView_android_fontFamily, 0)
        if (fontFamilyId > 0) {
            fontResId = fontFamilyId
        }
        val mTitle: String? = attr.getString(R.styleable.MenuItemView_title)
        val mSubtitle: String? = attr.getString(R.styleable.MenuItemView_subtitle)
        val mDescription: String? = attr.getString(R.styleable.MenuItemView_description)
        val mSubtitleVisibility: Boolean =
            attr.getBoolean(R.styleable.MenuItemView_showSubtitle, false)
        val mDescriptionVisibility: Boolean =
            attr.getBoolean(R.styleable.MenuItemView_showDescription, false)
        val mStartIconVisibility: Boolean =
            attr.getBoolean(R.styleable.MenuItemView_showStartIcon, true)
        val mEndIconVisibility: Boolean =
            attr.getBoolean(R.styleable.MenuItemView_showEndIcon, true)
        val mStartIcon: Drawable? = attr.getDrawable(R.styleable.MenuItemView_startIcon)
        val mEndIcon: Drawable? = attr.getDrawable(R.styleable.MenuItemView_endIcon)
        val mShowBottomDivider: Boolean = attr.getBoolean(R.styleable.MenuItemView_showBottomDivider, true)

        title = mTitle
        subTitle = mSubtitle
        description = mDescription
        showSubTitle = mSubtitleVisibility
        showDescription = mDescriptionVisibility
        showMenuIcon = mStartIconVisibility
        showEndIcon = mEndIconVisibility
        showBottomDivider = mShowBottomDivider
        menuIcon = mStartIcon
        endIcon = mEndIcon

        initView(ctx)
        attr.recycle()
    }

    private fun initView(mContext: Context) {
        val parent: View = View.inflate(mContext, R.layout.widget_menu_item, this)
        rootLayout = parent
        txtTitle = parent.findViewById(R.id.txtTitle)
        txtSubTitle = parent.findViewById(R.id.txtSubtitle)
        txtDescription = parent.findViewById(R.id.txtDescription)
        imgStartIcon = parent.findViewById(R.id.imgStartIcon)
        imgEndIcon = parent.findViewById(R.id.imgEndIcon)

        setValues()
    }

    private fun setValues() {
        txtTitle?.text = title
        txtSubTitle?.text = subTitle
        txtDescription?.text = description
        imgStartIcon?.setImageDrawable(menuIcon)
        imgEndIcon?.setImageDrawable(endIcon)

        txtSubTitle?.isVisible = showSubTitle
        txtDescription?.isVisible = showDescription
        imgStartIcon?.isVisible = showMenuIcon
        imgEndIcon?.isVisible = showEndIcon
        dividerBottom?.isVisible = showBottomDivider
    }

    private fun updateTextTypeface() {
        fontResId?.let { id ->
            if (id > 0) {
                val typeFace: Typeface? = ResourcesCompat.getFont(context, id)
                txtTitle?.typeface = typeFace
                txtSubTitle?.typeface = typeFace
                txtDescription?.typeface = typeFace
            }
        }
    }
}