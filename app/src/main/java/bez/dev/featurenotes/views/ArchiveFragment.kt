package bez.dev.featurenotes.views

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import bez.dev.featurenotes.R

class ArchiveFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_archive, container, false)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.archive_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    companion object {

        fun newInstance() = ArchiveFragment().apply {
//            arguments = Bundle().apply {
//                putInt(FRAGMENT_DATA, filterType) }
        }

    }


}