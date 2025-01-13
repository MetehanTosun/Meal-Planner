import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from 'pdfmake/build/vfs_fonts';

pdfMake.vfs = pdfFonts.pdfMake ? pdfFonts.pdfMake.vfs : pdfFonts.vfs;

export const generateShoppingListPDF = (shoppingList, startDate, endDate) => {
  // formats date into german date format
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('de-DE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  };

  const formattedStartDate = formatDate(startDate);
  const formattedEndDate = formatDate(endDate);

  // Defines the pdf
  const docDefinition = {
    pageSize: 'A4',
    pageMargins: [40, 40, 40, 40],
    content: [
      {
        text: 'Einkaufszettel',
        fontSize: 24,
        bold: true,
        alignment: 'center',
        margin: [0, 0, 0, 10]
      },
      {
        text: `${formattedStartDate} - ${formattedEndDate}`,
        fontSize: 14,
        alignment: 'center',
        margin: [0, 0, 0, 20],
        color: '#666666'
      },
      {
        table: {
          headerRows: 1,
          widths: ['*', '*', '*'],
          body: [
            [
              { text: 'Zutat', style: 'tableHeader' },
              { text: 'Menge', style: 'tableHeader' },
              { text: 'Einheit', style: 'tableHeader' }
            ],
            ...shoppingList.map(item => [
              item.name,
              { text: item.amount.toString(), alignment: 'right' },
              item.unit
            ])
          ]
        }
      }
    ],
    styles: {
      tableHeader: {
        bold: true,
        fontSize: 13,
        color: 'white',
        fillColor: '#333333',
        alignment: 'left',
        margin: [5, 5, 5, 5]
      }
    },
    defaultStyle: {
      fontSize: 12,
      columnGap: 20
    }
  };

  return pdfMake.createPdf(docDefinition);
};
