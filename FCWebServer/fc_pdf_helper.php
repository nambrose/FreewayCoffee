<?php
    /**
     * fc_pdf_helper.php
     *
     * 
     */
    
    //include("fc_constants.php");
	
    class PDFHelper
    {
        function GenerateOrderPDF($pdf_filename,$order_id, $user_name, $user_email, $order_cost, $time_needed, $tag_string)
        {
            //ORDER_PDF_PATH_PREFIX
            
            $pdf = $this->MakeBlankPDFLabel();
            
            $pdf->SetFont('Arial','B',16);
            $outstring = $user_name . " TAG: " . $tag_string;
            // No borders, and next line below this one.
            $pdf->Cell(2,0.25,$outstring,1,1,'L',false);
            
            $pdf->Cell(2,0.25,$time_needed,1,1,'L',false);
            
            $outstring = "Ord#: " . $order_id . " Cost $" . $order_cost;
            $pdf->SetFont('Arial','I',12);
            $pdf->Cell(2,0.25,$outstring,1,1,'L',false);
            
            $pdf->SetFont('Arial','I',10);
            $pdf->Cell(2,0.25,$user_email,1,1,'L',false);
                        
            $pdf->Close();
            $pdf->Output($pdf_filename,'F');
            
        }
        
        function MakeBlankPDFLabel()
        {
            $page_size = array(ORDER_PDF_WIDTH_INCHES,ORDER_PDF_HEIGHT_INCHES);
        
            $pdf = new FPDF('L','in',$page_size);
        
            $pdf->SetMargins(0,0);
            $pdf->SetAutoPageBreak(true,0);
            $pdf->AddPage();
            return $pdf;
        }
        
        function GenerateDrinkOrderPDF($pdf_filename, $order_id, $drink_id,$user_name,$time_needed,$drink_options_str,$drink_syrups)
        {
            $pdf = $this->MakeBlankPDFLabel();
            
            $pdf->SetFont('Arial','B',12);
            
            $outstring= $user_name . "TIME: " . $time_needed;
            
            $pdf->Cell(2,0.25,$outstring,1,1,'L',false);
            
            //print("PDF: {$drink_options_str}");
            
            $pdf->SetFont('Arial','',6);
            //$pdf->MultiCell(0,0.75,$drink_options_str,1,'L',false);
            $pdf->Cell(2,0.25,$drink_options_str,1,1,'L',false);
            $pdf->Cell(2,0.5,$drink_syrups,1,1,'L',false);
            $pdf->Close();
            //print("XFile: {$pdf_filename}");
                  
            $pdf->Output($pdf_filename,'F');

        }
    }
?>