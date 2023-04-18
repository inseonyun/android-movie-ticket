package woowacourse.movie.activities.ticketing

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.woowacourse.movie.domain.policy.DiscountDecorator
import woowacourse.movie.R
import woowacourse.movie.activities.movielist.MovieListActivity
import woowacourse.movie.activities.ticketingresult.TicketingResultActivity
import woowacourse.movie.extensions.exitForUnNormalCase
import woowacourse.movie.extensions.getParcelableCompat
import woowacourse.movie.extensions.showToast
import woowacourse.movie.model.MovieUI
import woowacourse.movie.model.ReservationUI
import woowacourse.movie.model.TicketUI
import woowacourse.movie.model.mapper.toMovie
import woowacourse.movie.model.mapper.toReservationUI
import woowacourse.movie.model.mapper.toTicket
import woowacourse.movie.model.mapper.toTicketUI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TicketingActivity : AppCompatActivity(), OnClickListener {
    private var movieTicket: TicketUI = TicketUI()

    private lateinit var movie: MovieUI
    private lateinit var movieDates: List<LocalDate>

    private val movieTimes = mutableListOf<LocalTime>()

    private val movieTimeAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(this@TicketingActivity, android.R.layout.simple_spinner_item, mutableListOf())
    }
    private var reservation: ReservationUI? = null
    private var selectedDate: LocalDate? = null
    private var selectedTime: LocalTime? = null

    private val textViewTicketCount: TextView by lazy {
        findViewById(R.id.tv_ticket_count)
    }
    private val spinnerMovieDate: Spinner by lazy {
        findViewById(R.id.spinner_movie_date)
    }
    private val spinnerMovieTime: Spinner by lazy {
        findViewById(R.id.spinner_movie_time)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticketing)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initIntentData()
        if (::movie.isInitialized) {
            setMovieInfo()
            setButtonOnClickListener()
            setMovieDateSpinnerAdapter()
            setMovieTimeSpinnerAdapter()
            setMovieDateSpinnerItemClick()
            setMovieTimeSpinnerItemClick()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreState(savedInstanceState)
        setTicketCount(movieTicket)
        val storedDateIndex = movieDates.indexOfFirst { it == selectedDate }
        spinnerMovieDate.setSelection(storedDateIndex)
        val storedTimeIndex = this.movieTimes.indexOfFirst { it == selectedTime }
        spinnerMovieTime.setSelection(storedTimeIndex)
    }

    private fun restoreState(savedInstanceState: Bundle) {
        savedInstanceState.run {
            reservation = getParcelableCompat<ReservationUI>(RESERVATION_KEY)?.apply {
                selectedDate = dateTime.toLocalDate().apply { initMovieTimes(this) }
                selectedTime = dateTime.toLocalTime()
                movieTicket = ticket
            }
        }
    }

    private fun initIntentData() {
        intent.getParcelableCompat<MovieUI>(MovieListActivity.MOVIE_KEY).run {
            if (this == null)
                exitForUnNormalCase(MESSAGE_EMPTY_MOVIE)
            else {
                movie = this
                movieDates = toMovie().getRunningDates()
            }
        }
    }

    private fun setMovieInfo() {
        with(movie) {
            thumbnail?.let { findViewById<ImageView>(R.id.iv_poster).setImageResource(it) }
            findViewById<TextView>(R.id.tv_title).text = title
            findViewById<TextView>(R.id.tv_date).text = getString(
                R.string.movie_release_date,
                startDate.formattedDate,
                endDate.formattedDate
            )
            findViewById<TextView>(R.id.tv_running_time).text =
                getString(R.string.movie_running_time, runningTime)
            findViewById<TextView>(R.id.tv_introduce).text = introduce
        }
    }

    private fun setTicketCount(ticket: TicketUI) {
        movieTicket = ticket
        textViewTicketCount.text = ticket.count.toString()
    }

    private fun setButtonOnClickListener() {
        val btnMinus: Button = findViewById(R.id.btn_minus)
        val btnPlus: Button = findViewById(R.id.btn_plus)
        val btnTicketing: Button = findViewById(R.id.btn_ticketing)

        btnMinus.setOnClickListener(this@TicketingActivity)
        btnPlus.setOnClickListener(this@TicketingActivity)
        btnTicketing.setOnClickListener(this@TicketingActivity)
    }

    private fun setMovieDateSpinnerAdapter() {
        spinnerMovieDate.adapter = ArrayAdapter(
            this@TicketingActivity,
            android.R.layout.simple_spinner_item,
            movieDates.map { it.format(DateTimeFormatter.ofPattern(MOVIE_DATE_PATTERN)) }
        )
    }

    private fun setMovieDateSpinnerItemClick() {
        spinnerMovieDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemView: View?,
                pos: Int,
                id: Long
            ) {
                selectedDate = movieDates[pos].apply {
                    initMovieTimes(this)
                }
                selectedTime = movieTimes.firstOrNull()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initMovieTimes(date: LocalDate) {
        with(movie) {
            movieTimes.clear()
            movieTimes.addAll(toMovie().getRunningTimes(date))
            movieTimeAdapter.clear()
            movieTimeAdapter.addAll(
                movieTimes.map { it.format(DateTimeFormatter.ofPattern(MOVIE_TIME_PATTERN)) }
            )
            movieTimeAdapter.notifyDataSetChanged()
        }
    }

    private fun setMovieTimeSpinnerAdapter() {
        spinnerMovieTime.adapter = movieTimeAdapter
    }

    private fun setMovieTimeSpinnerItemClick() {
        spinnerMovieTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemView: View?,
                pos: Int,
                id: Long
            ) {
                selectedTime = movieTimes[pos]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_minus -> {
                minusTicketCount()
            }
            R.id.btn_plus -> {
                plusTicketCount()
            }
            R.id.btn_ticketing -> {
                onClickTicketing()
            }
        }
    }

    private fun minusTicketCount() =
        setTicketCount(movieTicket.toTicket().run { dec().toTicketUI() })

    private fun plusTicketCount() =
        setTicketCount(movieTicket.toTicket().run { inc().toTicketUI() })

    private fun onClickTicketing() {
        if (selectedDate == null || selectedTime == null) {
            showToast(getString(R.string.select_date_and_time))
            return
        }
        reservation = reserveMovie()?.apply {
            val intent = Intent(this@TicketingActivity, TicketingResultActivity::class.java)
            intent.putExtra(RESERVATION_KEY, this)
            startActivity(intent)
            finish()
        }
    }

    private fun reserveMovie(): ReservationUI? {
        val reservationDateTime = LocalDateTime.of(selectedDate, selectedTime)

        return movie.toMovie().reserveMovie(
            reservationDateTime,
            movieTicket.toTicket(),
            calculateTicketTotalPrice(reservationDateTime)
        )?.run { toReservationUI() }
    }

    private fun calculateTicketTotalPrice(dateTime: LocalDateTime): Int =
        movieTicket.toTicket().calculatePrice(
            DiscountDecorator(dateTime).calculatePrice()
        )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@TicketingActivity, MovieListActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        reservation = ReservationUI(
            movie,
            LocalDateTime.of(selectedDate, selectedTime),
            movieTicket
        )
        outState.putParcelable(RESERVATION_KEY, reservation)
    }

    companion object {
        private const val MESSAGE_EMPTY_MOVIE = "영화 정보가 없습니다"

        private const val MOVIE_DATE_PATTERN = "yyyy.MM.dd"
        private const val MOVIE_TIME_PATTERN = "HH:mm"

        internal const val RESERVATION_KEY = "reservation"
    }
}
