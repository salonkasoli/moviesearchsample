package com.github.salonkasoli.moviesearchsample.detail.ui

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.facebook.drawee.view.SimpleDraweeView
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.ui.LoadingWidget
import com.github.salonkasoli.moviesearchsample.genre.Genre

class MovieDetailWidget(
    private val loadingWidget: LoadingWidget,
    private val movieDetailContainer: View,
    private val photo: SimpleDraweeView,
    private val title: TextView,
    private val year: TextView,
    private val genres: TextView,
    private val vote: TextView,
    private val userVote: TextView,
    private val voteDivider: View,
    private val overview: TextView,
    private val overviewDivider: View
) {

    /**
     * Дернется, если юзер нажмет "ПОВТОРИТЬ" во время показа ошибки загрузки.
     */
    var retryClickListener: (() -> Unit)? = null

    /**
     * Дернется, если юзер захочет авторизоваться.
     */
    var authClickListener: (() -> Unit)? = null

    /**
     * Дернется, когда юзер захочет оценить фильм. Это позволено только авторизованным юзерам.
     */
    var rateClickListener: (() -> Unit)? = null

    private val badVoteColor = ContextCompat.getColor(
        movieDetailContainer.context, R.color.movie_detail_bad_color
    )
    private val mediumVoteColor = ContextCompat.getColor(
        movieDetailContainer.context, R.color.movie_detail_medium_color
    )
    private val goodVoteColor = ContextCompat.getColor(
        movieDetailContainer.context, R.color.movie_detail_good_color
    )

    init {
        loadingWidget.retryClickListener = {
            retryClickListener?.invoke()
        }
    }

    fun showMovieDetail(movieDetail: MovieDetailUiModel) {
        movieDetailContainer.visibility = View.VISIBLE
        loadingWidget.hide()

        photo.setImageURI(movieDetail.posterUrl)
        title.text = movieDetail.title
        year.text = movieDetail.year
        genres.text = movieDetail.genres.joinToString { genre: Genre -> genre.name }

        vote.text = createVoteCharSequence(movieDetail.averageVote, movieDetail.voteCount)
        when (movieDetail.userVote) {
            MovieDetailUiModel.USER_VOTE_NO_AUTH -> {
                userVote.text = "Войдите чтобы оценить"
                userVote.setOnClickListener {
                    authClickListener?.invoke()
                }
            }
            MovieDetailUiModel.USER_VOTE_EMPTY -> {
                userVote.text = "Оценить"
                userVote.setOnClickListener {
                    rateClickListener?.invoke()
                }
            }
            MovieDetailUiModel.USER_VOTE_ERROR -> {
                userVote.text = "Не удалось загрузить оценку"
                userVote.isClickable = false
            }
            else -> {
                userVote.text = "Ваша оценка: ${movieDetail.userVote}"
                userVote.isClickable = false
            }
        }

        if (!movieDetail.overview.isNullOrBlank()) {
            overview.visibility = View.VISIBLE
            overview.text = movieDetail.overview
            overviewDivider.visibility = View.VISIBLE
        } else {
            overview.visibility = View.GONE
            overviewDivider.visibility = View.GONE
        }
    }

    private fun createVoteCharSequence(vote: Float, voteCount: Int): CharSequence {
        val spannable = SpannableStringBuilder("Оценка: $vote/10 ($voteCount)")
        val voteColor: Int = when {
            vote >= 7.5 -> goodVoteColor
            vote >= 6.5 -> mediumVoteColor
            else -> badVoteColor
        }
        spannable.setSpan(
            ForegroundColorSpan(voteColor), 8, 11, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD), 8, 11, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    fun showLoading() {
        loadingWidget.showLoading()
        movieDetailContainer.visibility = View.GONE
    }

    fun showError() {
        loadingWidget.showError()
        movieDetailContainer.visibility = View.GONE
    }

}