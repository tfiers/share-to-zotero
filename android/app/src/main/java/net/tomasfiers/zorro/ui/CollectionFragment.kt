package net.tomasfiers.zorro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import net.tomasfiers.zorro.ZorroApplication
import net.tomasfiers.zorro.databinding.CollectionFragmentBinding
import net.tomasfiers.zorro.viewmodels.CollectionViewModel
import net.tomasfiers.zorro.viewmodels.CollectionViewModelFactory
import timber.log.Timber

class CollectionFragment : Fragment() {

    private val navigationArgs: CollectionFragmentArgs by navArgs()
    private val viewModel: CollectionViewModel by viewModels {
        CollectionViewModelFactory(
            navigationArgs.collectionId,
            (activity?.application as ZorroApplication).dataRepo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CollectionFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val collectionClickListener = ListItemClickListener { collection ->
            findNavController().navigate(
                CollectionFragmentDirections.actionCollectionSelf(collection.key)
            )
        }
        val itemClickListener = ListItemClickListener { item ->
            Timber.i("Show item ${item.name}")
        }
        val adapter = RecyclerViewAdapter(collectionClickListener, itemClickListener)
        binding.recyclerView.adapter = adapter
        viewModel.listItems.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            binding.recyclerView.smoothScrollToPosition(0)
        })
        // Performance improvement (because changes in list content do not change layout size):
        binding.recyclerView.setHasFixedSize(true)
        binding.pullToRefresh.setOnRefreshListener { viewModel.syncLibrary() }
        viewModel.isSyncing.observe(viewLifecycleOwner, Observer { isSyncing ->
            binding.pullToRefresh.isRefreshing = isSyncing
        })
        //viewModel.collectionName.observe(viewLifecycleOwner, Observer { collectionName ->
        //    (activity as MainActivity).binding.toolbar.title = collectionName
        //})
        return binding.root
    }
}
