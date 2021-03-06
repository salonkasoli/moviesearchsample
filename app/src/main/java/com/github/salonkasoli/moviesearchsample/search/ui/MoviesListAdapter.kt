package com.github.salonkasoli.moviesearchsample.search.ui

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.github.salonkasoli.moviesearchsample.R
import com.github.salonkasoli.moviesearchsample.core.rv.BaseViewHolder
import com.github.salonkasoli.moviesearchsample.genre.Genre

class MoviesListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val movies = ArrayList<MovieUiModel>()

    var loadingState = LoadingState.GONE
    var errorClickListener: (() -> Unit)? = null
    var clickListener: ((MovieUiModel) -> Unit)? = null

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
        if (position < movies.size) {
            bindMovie(h as MovieItemHolder, movies[position])
        } else {
            bindLoading(h as LoadingHolder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FILM_VIEW_TYPE -> MovieItemHolder(parent)
            LOADING_VIEW_TYPE -> LoadingHolder(parent)
            else -> throw IllegalStateException("unknown view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < movies.size) {
            FILM_VIEW_TYPE
        } else {
            LOADING_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return if (loadingState == LoadingState.GONE) {
            movies.size
        } else {
            movies.size + 1
        }
    }

    private fun bindMovie(h: MovieItemHolder, movie: MovieUiModel) {
        h.title.text = movie.title
        h.year.text = movie.releaseDate
        h.photo.setImageURI(movie.posterPreviewUrl)
        h.photo.hierarchy.setFailureImage(R.drawable.ic_no_interner)
        h.photo.hierarchy.setPlaceholderImage(R.drawable.ic_waiting)
        h.genres.text = movie.genres.joinToString { genre: Genre -> genre.name }

        h.itemView.setOnClickListener {
            clickListener?.invoke(movie)
        }
    }

    private fun bindLoading(h: LoadingHolder) {
        when (loadingState) {
            LoadingState.LOADING -> {
                h.progressBar.visibility = View.VISIBLE
                h.errorText.visibility = View.GONE
                h.retryText.visibility = View.GONE
            }
            LoadingState.ERROR -> {
                h.progressBar.visibility = View.GONE
                h.errorText.visibility = View.VISIBLE
                h.retryText.visibility = View.VISIBLE
            }
            LoadingState.GONE -> {
                throw IllegalStateException("Loading holder while $loadingState")
            }
        }
        h.retryText.setOnClickListener {
            errorClickListener?.invoke()
        }
    }

    class MovieItemHolder(
        parent: ViewGroup
    ) : BaseViewHolder(parent, R.layout.movie_list_item_movie) {

        val photo: SimpleDraweeView = findView(R.id.photo)
        val title: TextView = findView(R.id.title)
        val year: TextView = findView(R.id.year)
        val genres: TextView = findView(R.id.genres)
    }

    class LoadingHolder(
        parent: ViewGroup
    ) : BaseViewHolder(parent, R.layout.movie_list_item_loading) {

        val progressBar: ProgressBar = findView(R.id.progress_bar)
        val errorText: TextView = findView(R.id.error_text)
        val retryText: TextView = findView(R.id.retry_text)
    }

    enum class LoadingState {
        GONE, LOADING, ERROR
    }

    companion object {
        private const val FILM_VIEW_TYPE = 1
        private const val LOADING_VIEW_TYPE = 2
    }
}