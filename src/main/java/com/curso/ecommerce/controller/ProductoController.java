package com.curso.ecommerce.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.IUsuarioService;
import com.curso.ecommerce.service.ProductoService;
import com.curso.ecommerce.service.UploadFileService;
import com.curso.ecommerce.service.UsuarioServiceImpl;

@Controller
@RequestMapping("/productos")
public class ProductoController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private UploadFileService upload;
	
    private final Cloudinary cloudinary;
    private String imageUrl;
    @Autowired
    public ProductoController(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

   /* @GetMapping("/")
    public String showUploadForm() {
        return "upload"; // Esta plantilla Thymeleaf mostrará el formulario para cargar imágenes
    }*/

    /*@PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            // Subir la imagen a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            // Agregar la URL de la imagen al modelo para visualización
            model.addAttribute("imageUrl", uploadResult.get("url"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "image"; // Esta plantilla Thymeleaf mostrará la imagen subida
    }
*/

	
	
	
	@GetMapping("")
	public String show(Model model) {
		model.addAttribute("productos", productoService.findAll());
		return "productos/show";
	}
	
	@GetMapping("/create")
	public String create() {
		return "productos/create";
	}
	
	@PostMapping("/save")
	public String save(Producto producto, @RequestParam("img") MultipartFile file, HttpSession session,Model model) throws IOException {
		LOGGER.info("Este es el objeto producto {}",producto);
		
		
		Usuario u= usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString() )).get();
		producto.setUsuario(u);	
		
		//imagen
		if (producto.getId()==null) { // cuando se crea un producto
			try {
	            // Subir la imagen a Cloudinary
	            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

	            // Agregar la URL de la imagen al modelo para visualización
	            model.addAttribute("imageUrl", uploadResult.get("img"));
	            imageUrl = (String) uploadResult.get("url");
				producto.setImagen((String)uploadResult.get("secure_url"));
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			
		}else {
			
		}
		
		productoService.save(producto);
		return "redirect:/productos";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		Producto producto= new Producto();
		Optional<Producto> optionalProducto=productoService.get(id);
		producto= optionalProducto.get();
		
		LOGGER.info("Producto buscado: {}",producto);
		model.addAttribute("producto", producto);
		
		return "productos/edit";
	}
	
	@PostMapping("/update")
	public String update(Producto producto, @RequestParam("img") MultipartFile file ) throws IOException {
		Producto p= new Producto();
		p=productoService.get(producto.getId()).get();
		
		if (file.isEmpty()) { // editamos el producto pero no cambiamos la imagem
			
			producto.setImagen(p.getImagen());
		}else {// cuando se edita tbn la imagen			
			//eliminar cuando no sea la imagen por defecto
			if (!p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			String nombreImagen= upload.saveImage(file);
			producto.setImagen(nombreImagen);
		}
		producto.setUsuario(p.getUsuario());
		productoService.update(producto);		
		return "redirect:/productos";
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		
		Producto p = new Producto();
		p=productoService.get(id).get();
		
		//eliminar cuando no sea la imagen por defecto
		if (!p.getImagen().equals("default.jpg")) {
			upload.deleteImage(p.getImagen());
            try {
				cloudinary.uploader().destroy(getPublicIdFromUrl(imageUrl), ObjectUtils.emptyMap());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            imageUrl = null; 
		}
		
		productoService.delete(id);
		return "redirect:/productos";
	}
	
    private String getPublicIdFromUrl(String url) {
        String[] parts = url.split("/");
        String fileName = parts[parts.length - 1];
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

	
	
	
	
	
}
