package net.tomasfiers.zoro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import net.tomasfiers.zoro.databinding.CollectionFragmentBinding

class CollectionFragment : Fragment() {

    private val viewModel: CollectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CollectionFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        val adapter = RecyclerViewAdapter(TreeItemClickListener { treeItem ->
            Toast.makeText(context, treeItem.toString(), Toast.LENGTH_SHORT).show()
        })
        binding.recyclerView.adapter = adapter
        viewModel.topLevelCollections.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        // Performance improvement (because changes in list content do not change layout size):
        binding.recyclerView.setHasFixedSize(true)

        return binding.root
    }
}
